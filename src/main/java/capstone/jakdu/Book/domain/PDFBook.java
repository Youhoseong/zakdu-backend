package capstone.jakdu.Book.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PDFBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "pdfBook")
    List<PDFKey> keys = new ArrayList<>();
}
