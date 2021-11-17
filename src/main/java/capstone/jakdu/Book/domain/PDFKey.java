package capstone.jakdu.Book.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PDFKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="pdfBook_id")
    private PDFBook pdfBook;

    private int pageNum;
    @Column(columnDefinition = "char(32)")
    private String decKey;
    @Column(columnDefinition = "char(16)")
    private String decIv;
}
