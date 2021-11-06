package capstone.jakdu.Book.service;


import capstone.jakdu.Book.object.MyTextPosition;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookRegisterService {

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
