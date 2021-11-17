package capstone.jakdu.Book.object.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookDownloadDto {
    private long id;
    private String title;
    private String fileName;
    // 표지파일 추가?
//    private FileType fileType;
    private byte[] bytes;
}
