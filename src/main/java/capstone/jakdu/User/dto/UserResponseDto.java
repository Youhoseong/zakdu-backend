package capstone.jakdu.User.dto;

import capstone.jakdu.User.domain.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private String userType;
    private String username;
    private String email;
    private String password;
    private Long point;
    public UserResponseDto(User entity) {
        this.username = entity.getUsername();
        this.email = entity.getEmail();
        this.password = entity.getPassword();
        this.userType = entity.getUserType();
        this.point = entity.getPoint();
    }
}
