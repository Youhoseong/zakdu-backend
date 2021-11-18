package capstone.jakdu.Book.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@Getter
public class PurchasedPageList {
    @Id
    Long id;
    // user relationship
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    PDFBook pdfBook;
    @ElementCollection
    private List<Boolean> pageList = new ArrayList<>();
}
