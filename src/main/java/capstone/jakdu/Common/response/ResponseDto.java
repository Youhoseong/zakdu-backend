package capstone.jakdu.Common.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ResponseDto {
    private StatusEnum statusEnum;
    private String message;
    private Object data;

}
