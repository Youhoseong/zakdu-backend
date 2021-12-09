package capstone.jakdu.Book.domain;

import capstone.jakdu.User.domain.User;
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

    public PurchasedPageList(PDFBook pdfBook, List<Boolean> purchasePageList, User user) {
        this.pdfBook = pdfBook;
        this.pageList = purchasePageList;
        this.user = user;
    }

    public static PurchasedPageList of(PDFBook pdfBook, List<Boolean> purchasePageList, User user) {
        return new PurchasedPageList(pdfBook,purchasePageList, user);
    }

    public void pageUpdate(List<Boolean> pageList) {
        for(int i=0; i<this.pageList.size(); i++ ) {
            if(!this.pageList.get(i)) {
                this.pageList.set(i, pageList.get(i));
            }
        }

    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // user relationship
    @ManyToOne(fetch = FetchType.LAZY)
    private PDFBook pdfBook;

    @ElementCollection
    private List<Boolean> pageList = new ArrayList<>();
}
