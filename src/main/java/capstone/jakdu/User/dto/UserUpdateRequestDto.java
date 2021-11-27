package capstone.jakdu.User.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequestDto {
    private String userType;
    private String username;
    private String email;
    private String password;
    private Long point;
    public UserUpdateRequestDto(String username, String email, String password, String userType, Long point) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.point = point;
    }
}
