package capstone.jakdu.EpubTest;

import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class EpubExample {

    Map<String, List<TOCReference>> fileNameTOCReferenceMap = new LinkedHashMap<>();

    Map<Integer, List<TOCReference>> indexTOCReferenceMap  = new LinkedHashMap<>();
    Map<Integer, List<String>> indexFileReferenceMap = new LinkedHashMap<>();

    @Test
    public void epubLibTest() throws IOException {
        // read epub file
        EpubReader epubReader = new EpubReader();
        Book book = epubReader.readEpub(new FileInputStream("pg48433-images.epub"));
        TableOfContents tableOfContents = book.getTableOfContents();
        
        
        // print the first title
        List<String> titles = book.getMetadata().getTitles();

        System.out.println("book title:" + (titles.isEmpty() ? "book has no title" : titles.get(0)));

        List<TOCReference> tocReferences = tableOfContents.getTocReferences();

        String href = book.getContents().get(0).getHref();
        System.out.println("href = " + href);

        book.getContents().forEach(content -> {
            System.out.println("content.getHref() = " + content.getHref());
        });

        Spine spine = book.getSpine();
        System.out.println("spine.getSpineReferences().get(0).getResourceId() = " + spine.getSpineReferences().get(0).getResourceId());
        System.out.println("spine.getResource = "+spine.getResource(0));
        System.out.println("spine.getResource = "+spine.getResource(1));
        System.out.println("spine.getResource = "+spine.getResource(2));
        String id = book.getContents().get(0).getId();
        System.out.println("id = " + id);



        byte[] data = book.getContents().get(6).getData();
       // System.out.println(" new String(data, StandardCharsets.UTF_8) = " +  new String(data, StandardCharsets.UTF_8));
        if(!tocReferences.isEmpty())
            printAllToc(tocReferences, 0);
        else
            System.out.println("비었음.");

        fileNameTOCReferenceMap.forEach((key, tocList) -> {
            System.out.println("key = " + key);
            tocList.forEach(toc -> {
                System.out.println("toc.getTitle() = " + toc.getTitle());
            });
        });

        int index = -1;

        for(int i=0; i<book.getContents().size(); i++) {
            Resource content = book.getContents().get(i);

            if(fileNameTOCReferenceMap.get(content.getHref()) != null) {
                List<TOCReference> tocReferenceList = new ArrayList<>(fileNameTOCReferenceMap.get(content.getHref()));
                List<String> hrefList = new ArrayList<>();
                hrefList.add(content.getHref());
                index++;
                indexTOCReferenceMap.put(index, tocReferenceList);
                indexFileReferenceMap.put(index, hrefList);
            }else {
                if(index < 0)
                    continue;
                indexFileReferenceMap.get(index).add(content.getHref());
            }
        }
        for (int i = 0; i <= index; i++) {
            System.out.println("i = " + i);
            indexFileReferenceMap.get(i).forEach(filename -> {
                System.out.println("filename = " + filename);
            });
            indexTOCReferenceMap.get(i).forEach(toc -> {
                System.out.println("toc.getTitle() = " + toc.getTitle());
            });

            System.out.println();
        }
    }
    // xhtml파일이름
    // xhtml파일이름#위치정보 a2K7
    // 아이템 (포함된 목차 제목들, 포함된 파일 이름들)
    // tocreference 돌고, 파일이름에 해당하는 목차 리스트 알수있고
    // contentsreference 돌면서, 특정 목차에 속해있는 파일 이름을 알 수 있음

    public void printAllToc(List<TOCReference> tocReferences, int hierarchyNum) {
        tocReferences.forEach(tocReference -> {
            List<TOCReference> referenceList = fileNameTOCReferenceMap.get(tocReference.getResource().getHref());
            if(referenceList == null) {
                referenceList = new ArrayList<>();
                fileNameTOCReferenceMap.put(tocReference.getResource().getHref(), referenceList);
            }
            referenceList.add(tocReference);

           // System.out.println(hierarchyNum + " / " + tocReference.getTitle() + " / " + tocReference.getResource().getHref() + " / " + tocReference.getResource().getSize());
            System.out.println(hierarchyNum + " / " + tocReference.getTitle());

            if(!tocReference.getChildren().isEmpty()) {
                printAllToc(tocReference.getChildren(), hierarchyNum+1);
            }
        });
    }
}
