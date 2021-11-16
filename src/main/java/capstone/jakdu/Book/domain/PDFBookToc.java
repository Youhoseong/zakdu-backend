package capstone.jakdu.Book.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PDFBookToc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int hierarchyNum;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PDFBook pdfBook;

    private int startPage;
    private int endPage;
    private String title;

}
