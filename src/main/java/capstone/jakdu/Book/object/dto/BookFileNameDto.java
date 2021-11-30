package capstone.jakdu.Book.object.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookFileNameDto {
    private long id;
    private String fileName;
    private String coverFileName;

}
