package capstone.jakdu.ocrtest;


import capstone.jakdu.Book.domain.PDFBookToc;
import capstone.jakdu.Book.repository.PDFBookTocRepository;
import capstone.jakdu.refactoring.MyPDFTextStripper;
import capstone.jakdu.refactoring.MyTextPositionComparatorMultiCol;
import capstone.jakdu.refactoring.Regex;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ocrexample {
    MyPDFTextStripper reader = new MyPDFTextStripper();

    @Autowired
    private PDFBookTocRepository pdfBookTocRepository;

    public ocrexample() throws IOException {
    }

    @Test
    @Transactional
    public void 목차분석_테스트() throws IOException {

        int startTocPage = 2; // page no.
        int colNum = 3;
        int lastTocPage = 2;
        String fileName = "./전공책/James Bradfield - Introduction to the economics of financial markets-Oxford University Press (2007).pdf";
        PDDocument document = PDDocument.load(new File(fileName));
        PDDocumentOutline outline =  document.getDocumentCatalog().getDocumentOutline();

        List<MyTextPosition> chunkWordList;
        if(outline != null) {
            chunkWordList = getTocFromBookmark(document, outline);
        }else {
            chunkWordList = getMultiColPageExtract(document, startTocPage, lastTocPage, colNum);
        }

        for(int i=0; i<chunkWordList.size(); i++) {
            MyTextPosition myTextPosition = chunkWordList.get(i);
            PDFBookToc pdfBookToc = PDFBookToc.builder()
                    .bookId(1L)
                    .hierarchyNum(myTextPosition.getHierarchyNum())
                    .startPage(myTextPosition.getStartPage())
                    .endPage(myTextPosition.getEndPage())
                    .title(myTextPosition.getText())
                    .build();

            pdfBookTocRepository.save(pdfBookToc);
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

    public void printBookmark(List<MyTextPosition> chunkWordList ,PDOutlineNode bookmark, String indentation, PDDocument document, int hierarchyNum) throws IOException
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


    public List<MyTextPosition> getMultiColPageExtract( PDDocument pdfDoc, int startTocPage, int lastTocPage, int colNum) throws IOException {



        System.out.println("separate:" + reader.getLineSeparator());
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


//        PDFBookToc pdfBookToc = PDFBookToc.builder()
//                .bookId(1L)
//                .hierarchyNum(1L)
//                .startPage(1L)
//                .endPage(3L)
//                .parentId(0L)
//                .build();
//
//        pdfBookTocRepository.save(pdfBookToc);

    }

    private List<MyTextPosition> getMyTextPositions(int i, int colNum, PDDocument pdfDoc) throws IOException {
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
            //if(current.getStartPage() == -1) continue;

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

    /**                Ⅲ   Ⅳ     ^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$
     * I, II, III ... => 정규식 ^[I, II, III, IV, V, VI, VII, VIII, IX, X, XI, XII]$
     * 1, 2, 3, 4 ....... => 정규식   ^[1-9]$
     * 01, 02, 03 ... => 정규식        ^\d{2}$
     * A, B, C, D ...                ^[A-Z]$
     * 가, 나, 다,                     ^(가|나|다|라|마|바|사|아|자|차|카|타|파|하)$
     * 유형 01, 유형 02, 유형 03, 유형 04 ...   ^유형$
     * •, ●                              ^(•|●).*$
     *  첫째, 둘째, 셋째 ... ^(첫|둘|셋|넷|다섯|여섯|일곱|여덟|아홉|열)째.*$
     * chapter 1, chapter 2, chapter 3 ...I, II, III ... ^(?i)chapter$
     *  1. ~~~~
     *   1[-, ., _] ...                         ^\d((-|.|_)\d)+.*$
     *   1-2.~~
     *  자신 = i
     * part 1, part 2, part 3 ...I, II, III ...     ^(?i)part$
     * 1장, 2장, 3장 ...                                   ^장$
     * appendix 1, appendix 2, appendix 3 ...I, II, III ... ^(?i)appendix$
     * section 1 section 2, section 3 ...I, II, II ... ^(?i)section$
     * case 1, case 2, case 3 ...I, II, III ...         ^(?i)case$
     *
     *
     *
     * 1, 2, 3 순차적 / chapter, 장 고정적
     * 순차적 | 순차적 + 고정적 | 고정적 + 순차적
     *
     *
     * h = 계층의 history 저장하는 Map
     * if fontSize[i-1] > fontSize[i] then 상위의 child, h map에 추가
     * elif fontSize[i-1] < fontSize[i] then h map 내부에서 찾기
     * else
     *      if x[i-1] < x[i] then 상위의 child
     *      elif x[i-1] > x[i] then 자신과 폰트 + 위치가 동일한 곳 찾기
     *      else then 자신과 같은 위치
     **/

   /**
     *
     * 1-13
     * 1. (교사용) 2022 수능특강 / 가능
     * 2.  [2018년개정] 개념원리~ / 가능
     * 3. [올림포스고난도]수학 스캔본 / 가능은 해보임
     * 4.  [유형필수]2019pdf해결의 법칙 /가능    *
     * 5. [unlocked]2020 자이스토리 / 애매함 *
     * 6. 02-1개념원리RPM / 가능
     * 7. 수력충전 / 가능
     * 8. 18짱중요한_수학 / 가능
     * 9. 2015개정중등수학3-1수력충전 / 불가해보임
     * 10. 2015개정중등수학3-2 ㅎ념플러~ / 불가해보임
     * 11. 2020자이스토리고2수학I / 애매함
     * 12. 2020짱중요한유형확률과통계 / 될듯한데 살짝 애매해보임
     * 13. 2021알피엠3_1 / 가능해보이는데 애매함
     *
     * 아샘HiMath기하 / 가능
     * 일등급수학I / 가능
     * 지학_풍산자반복수학_수학(상)_본문(학생용) / 가능
     * 짱쉬운유형 / 가능
     * 텐투_고등수학(하)_본문 / 가능
     * 풍산자반복수학중2-1본문 / 가능
     * Informationselfbook1(정보책) / 가능
     * RPM기하 / 가능
     *
     * 풍산자라이가확률과통계본문 / 불가
     * EBS2020학년도수능완성수학가형 / 불가
     * EBS2020학년도수능완성수학나형 / 불가
     * 중 1 중학 연산 1권 / 불가
     *
     *1. 4-7 불가
     * 2. 9종시크릿교과서 가능
     * 3. 개념원리수학하 가능
     * 4. 개념플러스유형수학2유형편 가능
     * 5. 고_미적분(홍성복)_지도서 불가능
     * 6. 고등_수학)개념플러스유형_확률과통계-개념편 애매
     * 7. 고등_수학)개념플러스유형_확률과통계-유형편
     * 8. 지학사 미적분 교사용 교과서 가능
     * 9. 수학의힘감마중1-1 가능
     * 10. 수학의힘베타중1-1 가능
     * 11. 수학의힘알파중1-1 가능
     * 12. 숨마쿰라우데고등스타트업(상) 가능 / 스캔본
     * 13. 수학의 바이블 가능 / 한글깨짐
     *
     * 백에 공백 지워야함!!!!
     *     */
}
