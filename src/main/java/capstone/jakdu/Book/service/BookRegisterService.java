package capstone.jakdu.Book.service;


import capstone.jakdu.Book.domain.PDFBookToc;
import capstone.jakdu.Book.object.HierarchyObject;
import capstone.jakdu.Book.object.MyTextPosition;
import capstone.jakdu.Book.repository.PDFBookTocRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookRegisterService {

    private final PDFBookTocRepository pdfBookTocRepository;

    public List<HierarchyObject> bookmarkAnalysisFromPdf(MultipartFile multipartFile) throws IOException {
        fileUpload(multipartFile);

        String fileName = "./pdfBook/" + multipartFile.getOriginalFilename();
        PDDocument document = PDDocument.load(new File(fileName));
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



}
