package capstone.jakdu.Book.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PurchasedPageList {

    public PurchasedPageList(PDFBook pdfBook, List<Boolean> purchasePageList) {
        this.pdfBook = pdfBook;
        this.pageList = purchasePageList;
        this.userId = 1L;
    }

    public static PurchasedPageList of(PDFBook pdfBook, List<Boolean> purchasePageList) {
        return new PurchasedPageList(pdfBook,purchasePageList);
    }

    public void pageUpdate(List<Boolean> pageList) {
        this.pageList = pageList;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long userId;

    // user relationship
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PDFBook pdfBook;

    @ElementCollection
    private List<Boolean> pageList = new ArrayList<>();
}
