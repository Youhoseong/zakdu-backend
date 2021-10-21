package capstone.jakdu.EpubTest;

import lombok.ToString;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpubExample {
    @Test
    public void epubLibTest() throws IOException {
        // read epub file
        EpubReader epubReader = new EpubReader();
        Book book = epubReader.readEpub(new FileInputStream("Weapons of Math Destruction How Big Data Increases Inequality and Threatens Democracy by Cathy O’Neil (z-lib.org).epub"));
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
    }
    // xhtml파일이름
    // xhtml파일이름#위치정보 a2K7
    public void printAllToc(List<TOCReference> tocReferences, int hierarchyNum) {
        tocReferences.forEach(tocReference -> {

            System.out.println(hierarchyNum + " toc.getTitle() = " + tocReference.getTitle() + " / " + tocReference.getResource().getHref() + " / " + tocReference.getFragmentId() +" / " + tocReference.getResource().getSize());

            if(!tocReference.getChildren().isEmpty()) {
                printAllToc(tocReference.getChildren(), hierarchyNum+1);
            }
        });
    }
}
