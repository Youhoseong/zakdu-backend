package capstone.jakdu.Book.object.dto;

import capstone.jakdu.Book.object.HierarchyObject;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class PdfBookPurchaseDto {
    //private String name;
    private List<Boolean> purchasePageList;
}
