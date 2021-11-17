package capstone.jakdu.Book.object.dto;


import capstone.jakdu.Book.object.HierarchyObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


// object mapper 사용시 no args constructor 있어야 함.
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookRegisterDto {
   private String category;
    private String name;
    private String author;
    private String publisher;
    private Date pubDate;
    private String intro;
    private Long price;
    private int realStartPage;

    private List<HierarchyObject> tocResult;


}
