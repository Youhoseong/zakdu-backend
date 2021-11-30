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

        System.out.println("userType : "+ userType);
        if(userType.equals("customer")) {
            System.out.println("Role_customer");
            this.userType = "ROLE_CUSTOMER";
        } else if(userType.equals("seller")){
            System.out.println("ROLE_SELLER");
            this.userType = "ROLE_SELLER";
        } else {
            System.out.println("userType");
            this.userType = userType;
        }
    }

    public static User of(
            String username,
            String email,
            String password,
            String userType,
            Long point){
            if(userType =="customer") {
                userType = "ROLE_CUSTOMER";
            } else if(userType == "seller"){
                userType = "ROLE_SELLER";
            }
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
