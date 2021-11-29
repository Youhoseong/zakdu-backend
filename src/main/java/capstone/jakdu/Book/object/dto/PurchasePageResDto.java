package capstone.jakdu.Book.object.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePageResDto {

    private List<Boolean> purchasePageList;
    private int pageCount;


}
