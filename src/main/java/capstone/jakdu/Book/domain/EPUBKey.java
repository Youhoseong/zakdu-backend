package capstone.jakdu.Book.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EPUBKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@ManyToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name="epubBook_id")
    //private EPUBBook epubBook;
    private Long epubBookId;

    private int sellGroup;
    @Column(columnDefinition = "char(32)")
    private String decKey;
    @Column(columnDefinition = "char(16)")
    private String decIv;
}
