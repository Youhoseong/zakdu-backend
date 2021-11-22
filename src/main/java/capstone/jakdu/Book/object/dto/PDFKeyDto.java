package capstone.jakdu.Book.object.dto;

import capstone.jakdu.Common.ByteArraySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PDFKeyDto {
    private int pageNum;
    private byte[] aesKey;
    private byte[] iv;

    @JsonSerialize(using= ByteArraySerializer.class)
    public byte[] getAesKey() {
        return aesKey;
    }

    @JsonSerialize(using= ByteArraySerializer.class)
    public byte[] getIv() {
        return iv;
    }
}


