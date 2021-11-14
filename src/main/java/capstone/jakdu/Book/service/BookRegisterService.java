package capstone.jakdu.Book.service;


import capstone.jakdu.Book.object.*;
import capstone.jakdu.Book.object.dto.PDFBookTocAnalyzeDto;
import capstone.jakdu.Book.repository.PDFBookTocRepository;
import capstone.jakdu.Common.function.Regex;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookRegisterService {

    private final PDFBookTocRepository pdfBookTocRepository;


    public List<HierarchyObject> zakduAnalysisFromPdf(MultipartFile multipartFile, PDFBookTocAnalyzeDto pdfBookTocAnalyzeDto) throws IOException {

        PDDocument document = PDDocument.load(multipartFile.getInputStream());


        List<MyTextPosition> myTextPositions = getTocFromZakduAnalyze(document, pdfBookTocAnalyzeDto);

        List<HierarchyObject> hierarchyObjects = convertToHierarchyData(myTextPositions);
        return hierarchyObjects;
    }

    public List<HierarchyObject> bookmarkAnalysisFromPdf(MultipartFile multipartFile) throws IOException {

        PDDocument document = PDDocument.load(multipartFile.getInputStream());
        PDDocumentOutline outline =  document.getDocumentCatalog().getDocumentOutline();
        List<HierarchyObject> hierarchyObjects = new ArrayList<>();

        if(outline == null){
            document.close();
            return hierarchyObjects;
        } else {
            List<MyTextPosition> myTextPositions = getTocFromBookmark(document, outline);

            hierarchyObjects = convertToHierarchyData(myTextPositions);
            printHirarchyChunkList(hierarchyObjects, "", 0);

            return hierarchyObjects;
        }

    }

    public List<HierarchyObject> convertToHierarchyData(List<MyTextPosition> chunkWordList) {
        List<HierarchyObject> hierarchyObjects = new ArrayList<>();

        for (int i=0; i<chunkWordList.size(); i++) {
            if(!chunkWordList.get(i).isConverted())  {
                resursiveConvert(hierarchyObjects, chunkWordList, i);
            }

        }

        return hierarchyObjects;

    }

    public void printHirarchyChunkList(List<HierarchyObject> current, String indentation, int hNum) {

        if(current == null)
            return;
        else {
            for(int i=0; i<current.size(); i++) {
                System.out.println(indentation + hNum + " "+current.get(i).getText());
                if(current.get(i).getChilds() != null)
                    printHirarchyChunkList(current.get(i).getChilds(), indentation + "    ", hNum+1);

            }
        }
    }




    public void resursiveConvert(List<HierarchyObject> hierarchyObjects, List<MyTextPosition> chunkWordList, int index) {
        if(index == chunkWordList.size()-1) {
            MyTextPosition myTextPosition1 = chunkWordList.get(index);
            myTextPosition1.setConverted(true);
            HierarchyObject hierarchyObject = new HierarchyObject(index, myTextPosition1.getText(), null);
            hierarchyObjects.add(hierarchyObject);
        } else {

            MyTextPosition myTextPosition1 = chunkWordList.get(index);

            HierarchyObject hierarchyObject = new HierarchyObject(index, myTextPosition1.getText(), null);
            hierarchyObjects.add(hierarchyObject);
            myTextPosition1.setConverted(true);

            for(int i=index+1; i<chunkWordList.size(); i++) {
                MyTextPosition myTextPosition2 = chunkWordList.get(i);
                if (!myTextPosition2.isConverted()) {
                    if (myTextPosition1.getHierarchyNum() < myTextPosition2.getHierarchyNum()) {

                        if (hierarchyObject.getChilds() == null) {
                            hierarchyObject.setChilds(new ArrayList<>());
                        }
                        resursiveConvert(hierarchyObject.getChilds(), chunkWordList, i);
                    } else if (myTextPosition1.getHierarchyNum() == myTextPosition2.getHierarchyNum()) {
                        myTextPosition2.setConverted(true);

                        myTextPosition1 = chunkWordList.get(i);
                        hierarchyObject = new HierarchyObject(i, myTextPosition2.getText(), null);
                        hierarchyObjects.add(hierarchyObject);

                    } else {
                        return;
                    }



                }
            }


        }

    }

    public void fileUpload(MultipartFile multipartFile) throws IOException {
        String filePath = System.getProperty("user.dir")+ "/pdfBook/";

        File dir = new File(filePath);
        if(!dir.exists())
            dir.mkdirs();

        if(!new File(filePath + multipartFile.getOriginalFilename()).exists()) {
            filePath =  filePath + multipartFile.getOriginalFilename();

            File f = new File(filePath);
            multipartFile.transferTo(f);

        }

    }

    public List<MyTextPosition> getTocFromBookmark(PDDocument document, PDDocumentOutline outline) throws IOException {

        List<MyTextPosition> chunkWordList = new ArrayList<>();

        printBookmark(chunkWordList,outline, "", document, 0);
        int pageNum = document.getNumberOfPages();
        for(int j = 0; j < chunkWordList.size(); j++) {
            MyTextPosition current = chunkWordList.get(j);
            if(current.getEndPage() == -1) {
                setEndPages(chunkWordList, j);
            }

            if(current.getStartPage() != -1 && current.getEndPage() == -1) {
                current.setEndPage(pageNum);
            }
        }

        for (int i = 0; i < chunkWordList.size(); i++) {
            MyTextPosition temp = chunkWordList.get(i);
            System.out.println(temp.getHierarchyNum() + " " + temp.getText() + " " + temp.getStartPage() + " " + temp.getEndPage());
        }


        document.close();
        return chunkWordList;
    }

    public List<MyTextPosition> getTocFromZakduAnalyze(PDDocument pdfDoc, PDFBookTocAnalyzeDto pdfBookTocAnalyzeDto) throws IOException {

        int startTocPage = pdfBookTocAnalyzeDto.getBookPDFTocStartPage();
        int lastTocPage = pdfBookTocAnalyzeDto.getBookPDFTocEndPage();
        int colNum = pdfBookTocAnalyzeDto.getBookPDFRowCount();



        List<MyTextPosition> chunkWordList = new ArrayList<>();
        for(int j = startTocPage; j <= lastTocPage; j++)
            chunkWordList.addAll(getMyTextPositions(j, colNum, pdfDoc));


        deleteEmptyChunk(chunkWordList);
        joinSeperatedPrefixAndPageNumber(chunkWordList);
        Map<HierarchyData, Boolean> pageExistDB = getPageExist(chunkWordList);
        joinSeperatedPageNumber(chunkWordList, pageExistDB);
        setHierarchy(chunkWordList);

        for(int j = 0; j < chunkWordList.size(); j++) {
            MyTextPosition chuckWord = chunkWordList.get(j);
            String text = chuckWord.getText();
            int hierarchyNumber = chuckWord.getHierarchyNum();
            int prefixNumber = chuckWord.getPrefixId();
            HierarchyData hierarchyData = new HierarchyData(chuckWord.getFontSize(), chuckWord.getPrefixId());
            Boolean pageExist = pageExistDB.get(hierarchyData);
            System.out.println("[isPageExist, hierarchyNum, prefixNum]:" + pageExist + "/ " + hierarchyNumber + "/ "+prefixNumber + "/ " + text);
        }

        setStartPageNumber(chunkWordList);

        for(int j = 0; j < chunkWordList.size(); j++) {
            MyTextPosition chuckWord = chunkWordList.get(j);
            String text = chuckWord.getText();
            int hierarchyNumber = chuckWord.getHierarchyNum();
            int prefixNumber = chuckWord.getPrefixId();
            HierarchyData hierarchyData = new HierarchyData(chuckWord.getFontSize(), chuckWord.getPrefixId());
            Boolean pageExist = pageExistDB.get(hierarchyData);
            System.out.println("[isPageExist, hierarchyNum, prefixNum]:" + pageExist + "/ " + hierarchyNumber + "/ "+prefixNumber + "/ " + text + "/ " + chuckWord.getStartPage());
        }

        System.out.println("-------------------------------");

        for(int j = 0; j < chunkWordList.size(); j++) {
            MyTextPosition current = chunkWordList.get(j);
            if(current.getStartPage() == -1) {
                setEmptyStartPageNumber(chunkWordList, j);
            }
        }

        int pageNum = pdfDoc.getNumberOfPages();
        for(int j = 0; j < chunkWordList.size(); j++) {
            MyTextPosition current = chunkWordList.get(j);
            if(current.getEndPage() == -1) {
                setEndPages(chunkWordList, j);
            }

            if(current.getStartPage() != -1 && current.getEndPage() == -1) {
                current.setEndPage(pageNum);
            }
        }
        System.out.println("pageExist / hierarchyNumber / prefixNumber / title / startPage / endPage");
        for(int j = 0; j < chunkWordList.size(); j++) {
            MyTextPosition chuckWord = chunkWordList.get(j);
            String text = chuckWord.getText();
            int hierarchyNumber = chuckWord.getHierarchyNum();
            int prefixNumber = chuckWord.getPrefixId();
            HierarchyData hierarchyData = new HierarchyData(chuckWord.getFontSize(), chuckWord.getPrefixId());
            Boolean pageExist = pageExistDB.get(hierarchyData);

            System.out.println(pageExist + "/ " + hierarchyNumber + "/ "+prefixNumber + "/ " + text
                    + "/ " + chuckWord.getStartPage() + "/ " + chuckWord.getEndPage());
        }

        pdfDoc.close();
        return chunkWordList;

    }
    private int setEmptyStartPageNumber(List<MyTextPosition> chunkWordList, int index) {
        if(chunkWordList.get(index).getStartPage() != -1) { //페이지 있으면
            return chunkWordList.get(index).getStartPage();
        }else {
            if(index == chunkWordList.size() - 1){
                return -1;
            }

            if(chunkWordList.get(index).getHierarchyNum() < chunkWordList.get(index+1).getHierarchyNum()){
                int pageNum = setEmptyStartPageNumber(chunkWordList, index+1);
                chunkWordList.get(index).setStartPage(pageNum);

                return pageNum;
            }
            else if(index > 0 && chunkWordList.get(index - 1).getHierarchyNum() == chunkWordList.get(index).getHierarchyNum()) {
                int pageNum = setEmptyStartPageNumber(chunkWordList, index - 1);
                chunkWordList.get(index).setStartPage(pageNum);

                return pageNum;
            }
            return -1;

        }


    }

    public void printBookmark(List<MyTextPosition> chunkWordList , PDOutlineNode bookmark, String indentation, PDDocument document, int hierarchyNum) throws IOException
    {
        PDOutlineItem current = bookmark.getFirstChild();
        while (current != null)
        {
            PDPage currentPage = current.findDestinationPage(document);
            Integer pageNumber = document.getDocumentCatalog().getPages().indexOf(currentPage) + 1;

            chunkWordList.add(new MyTextPosition(current.getTitle(), pageNumber,-1, hierarchyNum));

            printBookmark(chunkWordList, current, indentation + "    ", document, hierarchyNum+1);
            current = current.getNextSibling();
        }
    }
    private List<MyTextPosition> getMyTextPositions(int i, int colNum, PDDocument pdfDoc) throws IOException {

        MyPDFTextStripper reader = new MyPDFTextStripper();
        reader.reset();
        reader.setStartPage(i);
        reader.setEndPage(i);
        String pageText = reader.getText(pdfDoc);
        List<MyTextPosition> myTextPositions = reader.getMyTextPositions();


        System.out.println(pageText);
        System.out.println("===========");
        System.out.println("xMax, xMin :" + reader.getXMax()+ " " + reader.getXMin());

        myTextPositions.sort(new MyTextPositionComparatorMultiCol(reader.getXMax() - reader.getXMin(), colNum, reader.getXMin()));


        // 같은 행 판단 start
        setLineNumber(myTextPositions);


        // 문자열로 묶기
        List<MyTextPosition> chunkWordList = joinSameLineCharact(myTextPositions);

        for(int j=0; j<chunkWordList.size(); j++) {
            System.out.println(chunkWordList.get(j).getText());

        }

        System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
        return chunkWordList;
    }
    private int setEndPages(List<MyTextPosition> chunkWordList, int index) {
        int endPage = -1;
        if(index == chunkWordList.size() - 1)
            return chunkWordList.get(index).getStartPage();

        MyTextPosition start = chunkWordList.get(index);
        for(int i = index + 1; i < chunkWordList.size(); i++) {
            MyTextPosition current = chunkWordList.get(i);
            if(current.getEndPage() != -1) continue;

            if(current.getHierarchyNum() <= start.getHierarchyNum()) {
                endPage = Math.max(start.getStartPage(), current.getStartPage());
                if(start.getStartPage() != -1)
                    start.setEndPage(endPage);
                return endPage;
            }
            else {
                endPage = Math.max(endPage, setEndPages(chunkWordList, i));
            }
        }
        return -1;

    }


    private void setStartPageNumber(List<MyTextPosition> chunkWordList) {
        for(int j=0; j<chunkWordList.size(); j++) {
            chunkWordList.get(j).setStartPage(chunkWordList.get(j).removeStartPage());
        }
    }


    private void joinSeperatedPageNumber(List<MyTextPosition> chunkWordList, Map<HierarchyData, Boolean> pageExistDB) {
        for(int j = 0; j < chunkWordList.size()-1; j++) {
            MyTextPosition chuckWord = chunkWordList.get(j);
            MyTextPosition nextChuckWord = chunkWordList.get(j+1);
            HierarchyData hierarchyData = new HierarchyData(chuckWord.getFontSize(), chuckWord.getPrefixId());
            if(pageExistDB.get(hierarchyData) && !Regex.hasPageNum(chuckWord.getText())) {
                if(nextChuckWord.getPrefixId() == 10 && Regex.hasPageNum(nextChuckWord.getText())) {
                    chuckWord.setText(chuckWord.getText() + " " + nextChuckWord.getText());
                    chunkWordList.remove(j+1);
                }
            }
        }
    }

    private void setHierarchy(List<MyTextPosition> chunkWordList) {
        int hierarchyNum = 0;
        Map<HierarchyData, Integer> hierarchyDB = new HashMap<>();

        MyTextPosition previousWord = chunkWordList.get(0);
        for(int j = 0; j < chunkWordList.size(); j++) {
            MyTextPosition chunkWord = chunkWordList.get(j);
            HierarchyData hierarchyData = new HierarchyData(chunkWord.getFontSize(), chunkWord.getPrefixId());
            Integer hNum = hierarchyDB.get(hierarchyData);
            if(hNum == null) { // 1이 최상위 계층
                if(!Regex.isStartPrefix(chunkWord.getText(),chunkWord.getPrefixId())) {
                    Iterator<HierarchyData> iterator = hierarchyDB.keySet().iterator();
                    HierarchyData minFontGapHierarchyData = null;
                    float minFontSizeGap = 1000000f;
                    while(iterator.hasNext()) {
                        HierarchyData key = iterator.next();
                        if(key.getPrefixNum() == chunkWord.getPrefixId()) {
                            if(minFontGapHierarchyData == null) {
                                minFontGapHierarchyData = key;
                                minFontSizeGap = fontSizeGap(chunkWord.getFontSize(), key);
                            }else {
                                if(minFontSizeGap > fontSizeGap(chunkWord.getFontSize(), key)) {
                                    minFontSizeGap = fontSizeGap(chunkWord.getFontSize(), key);
                                    minFontGapHierarchyData = key;
                                }
                            }
                        }
                    }

                    hNum = hierarchyDB.get(minFontGapHierarchyData);
                }


                // 계층이 2이상 차이나면
                if(j != 0 && hierarchyNum+1 > previousWord.getHierarchyNum()+1 && chunkWord.getPrefixId() != 10) {
                    Iterator<HierarchyData> iterator = hierarchyDB.keySet().iterator();
                    HierarchyData minFontGapHierarchyData = null;
                    float minFontSizeGap = 1000000f;
                    while(iterator.hasNext()) {
                        HierarchyData key = iterator.next();
                        if(key.getPrefixNum() == chunkWord.getPrefixId()) {
                            if(minFontGapHierarchyData == null) {
                                minFontGapHierarchyData = key;
                                minFontSizeGap = fontSizeGap(chunkWord.getFontSize(), key);
                            }else {
                                if(minFontSizeGap > fontSizeGap(chunkWord.getFontSize(), key)) {
                                    minFontSizeGap = fontSizeGap(chunkWord.getFontSize(), key);
                                    minFontGapHierarchyData = key;
                                }

                            }
                        }
                    }

                    hNum = hierarchyDB.get(minFontGapHierarchyData);

                }
                if(hNum == null){
                    hierarchyDB.put(hierarchyData, ++hierarchyNum);
                    hNum = hierarchyNum;
                }

            }
            previousWord = chunkWord;
            System.out.println("chunkWord = " + chunkWord.getText() + " / " + chunkWord.getPrefixId());
            chunkWord.setHierarchyNum(hNum);
        }

    }

    private float fontSizeGap(float fontSize, HierarchyData data2) {
        return Math.abs(fontSize - data2.getFontSize());
    }

    private Map<HierarchyData, Boolean> getPageExist(List<MyTextPosition> chunkWordList) {
        Map<HierarchyData, Boolean> pageExistDB = new HashMap<>();

        for(int j = 0; j < chunkWordList.size(); j++) {
            MyTextPosition chuckWord = chunkWordList.get(j);
            HierarchyData hierarchyData = new HierarchyData(chuckWord.getFontSize(), chuckWord.getPrefixId());

            Boolean pageExist = pageExistDB.get(hierarchyData);
            boolean hasPage = Regex.hasPageNum(chuckWord.getText());
            if(pageExist == null) {
                pageExistDB.put(hierarchyData, hasPage);
            }
            else {
                if(hasPage && !pageExistDB.get(hierarchyData)) {
                    pageExistDB.put(hierarchyData, true) ;
                }
            }
        }
        return pageExistDB;
    }

    private void joinSeperatedPrefixAndPageNumber(List<MyTextPosition> chunkWordList) {
        List<MyTextPosition> prefixHTextPositionList = new ArrayList<>();
        for(int j = 0; j < chunkWordList.size(); j++) {
            String text = chunkWordList.get(j).getText();
            int prefixNumber = Regex.getPrefixNum(text);
            if (prefixNumber == 3 && text.matches("^H((\\s|\\.).*)?$")){
                prefixHTextPositionList.add(chunkWordList.get(j));
                System.out.println("[H prefixNumber]============================================================================: " + text);
            }
            else if(prefixNumber == 1 && text.matches("^I((\\s|\\.).*)?$")) {
                for(int i=0; i< prefixHTextPositionList.size(); i++) {
                    if(prefixHTextPositionList.get(i).getFontSize() == chunkWordList.get(j).getFontSize()) {
                        prefixNumber = prefixHTextPositionList.get(i).getPrefixId();
                        prefixHTextPositionList.remove(i);
                        break;
                    }
                }
            }
            System.out.println("[prefixNumber]: " + prefixNumber + "/ " + text);
            chunkWordList.get(j).setPrefixId(prefixNumber);
        }

        int oneDigitNumberPrefixCount = 0;
        int twoDigitNumberPrefixCount = 0;

        for(int j = 0; j < chunkWordList.size()-1; j++) {
            MyTextPosition myTextPosition = chunkWordList.get(j);
            MyTextPosition nextTextPosition = chunkWordList.get(j+1);

            int currentPrefixId = myTextPosition.getPrefixId();

            if(currentPrefixId == 2)
                oneDigitNumberPrefixCount++;
            else if(currentPrefixId == 0)
                twoDigitNumberPrefixCount++;

            // 접두어가 혼자있을때
            if(Regex.isAllTextIsPrefix(myTextPosition.getText(), myTextPosition.getPrefixId())) {
                boolean flag = false;

                if(currentPrefixId == 2) {
                    // string을 숫자로 바꿔
                    int num = Integer.parseInt(myTextPosition.getText());
                    if(oneDigitNumberPrefixCount != Integer.parseInt(myTextPosition.getText())) {
                        oneDigitNumberPrefixCount--;
                        flag = true;
                    }else if(num == 1 && oneDigitNumberPrefixCount != 1){
                        oneDigitNumberPrefixCount = 1;
                    }

                }
                else if(currentPrefixId == 0) {
                    int num = Integer.parseInt(myTextPosition.getText());
                    if(twoDigitNumberPrefixCount != num) {
                        twoDigitNumberPrefixCount--;
                        flag = true;
                    }else if(num == 1 && twoDigitNumberPrefixCount != 1){
                        twoDigitNumberPrefixCount = 1;
                    }

                }

                if(!flag) {
                    if (nextTextPosition.getPrefixId() == 10) {
                        chunkWordList.set(j + 1,
                                new MyTextPosition(chunkWordList.get(j).getFontSize(),
                                        chunkWordList.get(j).getX(),
                                        chunkWordList.get(j).getText().concat(" " + chunkWordList.get(j + 1).getText())));

                        chunkWordList.get(j + 1).setPrefixId(chunkWordList.get(j).getPrefixId());
                        chunkWordList.remove(j);
                    }
                }
                else {
                    chunkWordList.get(j).setPrefixId(10);
                }
            }
        }
    }

    private void deleteEmptyChunk(List<MyTextPosition> chunkWordList) {
        for(int j = 0; j< chunkWordList.size(); j++) {
            // 공백만 있는 덩어리 제거
            if(chunkWordList.get(j).getText().isBlank()) {
                chunkWordList.remove(j);
                j--;
            } else {
                chunkWordList.get(j).setText(
                        chunkWordList.get(j).getText().trim()
                );
            }
        }
    }

    private List<MyTextPosition> joinSameLineCharact(List<MyTextPosition> myTextPositions) {
        String sameLineTextString = "";
        int before = myTextPositions.get(0).getId();
        List<MyTextPosition> chunkWordList = new ArrayList<>();

        float fontSize = myTextPositions.get(0).getFontSize();
        float X = myTextPositions.get(0).getX();
        for (int j = 0; j < myTextPositions.size(); j++) {
            MyTextPosition myTextPosition = myTextPositions.get(j);

            if(myTextPosition.getId() != before) {
                System.out.println("");
                before = myTextPosition.getId();
                sameLineTextString = sameLineTextString.replaceAll("((\\.\\s){3,}|(\\.{3,}))","... ");
                chunkWordList.add(new MyTextPosition(fontSize, X, sameLineTextString.replaceAll("(\\s| )+", " ")));
                sameLineTextString = "";
                fontSize = myTextPosition.getFontSize();
                X = myTextPosition.getX();
            }

            System.out.print(myTextPosition.getText());
            if(sameLineTextString.matches("(\\s| )+")) {
                fontSize = myTextPosition.getFontSize();
            }

            sameLineTextString += myTextPosition.getText();


        }
        sameLineTextString = sameLineTextString.replaceAll("((\\.\\s){3,}|(\\.{3,}))","... ");
        chunkWordList.add(new MyTextPosition(fontSize,X, sameLineTextString.replaceAll("(\\s| )+", " ")));
        return chunkWordList;
    }

    private void setLineNumber(List<MyTextPosition> myTextPositions) {
        int id = 0;
        myTextPositions.get(0).setId(id);
        MyTextPosition startChar = myTextPositions.get(0);
        MyTextPosition tempChar = null;
        int tempStart = -1;
        for(int j = 1; j < myTextPositions.size(); j++) {
            MyTextPosition here = myTextPositions.get(j);
            // here가 startChar와 같은 행이라고 판단되는 경우
            if (Math.abs(startChar.getY() - here.getY()) < Math.min(startChar.getHeight(), here.getHeight())) {
                here.setId(id);
                if(tempStart >= 0 && tempChar != null) {
                    for(int k = tempStart; k < j; k++) {
                        myTextPositions.get(k).setId(id);
                    }
                    tempStart = -1;
                    tempChar = null;
                }
                else {
                    here.setId(id);
                }
            }
            // ex)분모 a
            else {
                if(tempChar != null) {
                    if (!(Math.abs(tempChar.getY() - here.getY()) < Math.min(tempChar.getHeight(), here.getHeight()))) {
                        id++;
                        for(int k = tempStart; k < j; k++) {
                            myTextPositions.get(k).setId(id);
                        }
                        startChar = tempChar;
                        tempStart = j;
                        tempChar = here;
                    }
                }
                else {
                    tempStart = j;
                    tempChar = here;
                }
            }
        }
        id++;
        for(int k = tempStart; k < myTextPositions.size(); k++) {
            myTextPositions.get(k).setId(id);
        }
    }



}
