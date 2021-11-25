package capstone.jakdu.User.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSaveRequestDto {
    private String userType;
    private String username;
    private String email;
    private String password;
    private Long point;
//    public UserSaveRequestDto(String username, String email, String password, String userType, Long point) {
//        this.username = username;
//        this.email = email;
//        this.password = password;
//        this.userType = userType;
//        this.point = point;
//    }
//    public User toEntity() {
//        return User.builder()
//                .username(username)
//                .email(email)
//                .password(password)
//                .userType(userType)
//                .point(point)
//                .build();
//    }
}
