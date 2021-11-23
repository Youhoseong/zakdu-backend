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

    public HierarchyObject(int id, String text, int startPage, int endPage, List<HierarchyObject> childs) {
        this.id = id;
        this.text = text;
        this.startPage = startPage;
        this.endPage = endPage;
        this.childs = childs;
    }


    private int id;
    private String text;
    private int startPage;
    private int endPage;
    private boolean tick;
    private List<HierarchyObject> childs;

}
