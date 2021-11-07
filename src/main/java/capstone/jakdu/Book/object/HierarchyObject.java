package capstone.jakdu.Book.object;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@Setter
public class HierarchyObject {

    private int id;
    private String text;
    private List<HierarchyObject> childs;

}
