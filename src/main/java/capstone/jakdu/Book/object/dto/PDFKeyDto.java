package capstone.jakdu.Book.object.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PDFKeyDto {
    private Long bookId;
    private int page;
    private byte[] aesKey;
    private byte[] aesIv;
}
