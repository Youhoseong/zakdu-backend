package capstone.jakdu.Book.object.dto;

import capstone.jakdu.Book.domain.PDFBook;
import lombok.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.util.Date;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
public class BookResponseDto {

    public BookResponseDto(PDFBook pdfBook, byte[] resource) {
        this.id = pdfBook.getId();
        this.category = pdfBook.getCategory();
        this.name = pdfBook.getName();
        this.author = pdfBook.getAuthor();
        this.publisher = pdfBook.getPublisher();
        this.pubDate = pdfBook.getPubDate();
        this.intro = pdfBook.getIntro();
        this.price = pdfBook.getPrice();
        this.bookCoverResource = resource;
        this.realStartPage = pdfBook.getRealStartPage();
        this.pdfPageCount = pdfBook.getPdfPageCount();
    }
    private Long id;
    private String category;
    private String name;
    private String author;
    private String publisher;
    private Date pubDate;
    private String intro;
    private Long price;
    private int realStartPage;
    private int pdfPageCount;

    private byte[] bookCoverResource;

}
