package capstone.jakdu.Book.domain;

import javax.persistence.*;

@Entity
public class PDFKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="pdfBook_id")
    private PDFBook pdfBook;

    private int pageNum;
    @Column(length = 32)
    private String decKey;
    @Column(length = 16)
    private String decIv;
}
