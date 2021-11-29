package capstone.jakdu.User.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private  String password;

    @Column(name = "user_type")
    private  String userType;

    @Column(name = "user_point")
    private Long point;

    public User(String username, String email, String password, String userType, Long point){
        this.username = username;
        this.email = email;
        this.password = "{noop}"+password;
        this.point = point;

        if(userType =="customer") {
            this.userType = "ROLE_CUSTOMER";
        } else if(userType == "seller"){
            this.userType = "ROLE_SELLER";
        }
    }

    public static User of(
            String username,
            String email,
            String password,
            String userType,
            Long point){
        return new User(username,email,password, userType,point);
    }



//    public void update(String username, String email, String password, String userType, Long point){
//        this.username = username;
//        this.email = email;
//        this.password = password;
//        this.userType = userType;
//        this.point = point;
//    }

}
