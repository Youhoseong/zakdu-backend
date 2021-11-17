package capstone.jakdu.Book.object;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class HierarchyObject {

    private int id;
    private String text;
    private int startPage;
    private int endPage;
    private List<HierarchyObject> childs;

}
