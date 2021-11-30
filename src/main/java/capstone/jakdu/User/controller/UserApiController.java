package capstone.jakdu.User.controller;

import capstone.jakdu.User.domain.AuthRequest;
import capstone.jakdu.User.domain.User;
import capstone.jakdu.User.dto.DuplicateEmailCheckDto;
import capstone.jakdu.User.dto.UserSaveRequestDto;
import capstone.jakdu.User.repository.UserInfoRepository;
import capstone.jakdu.User.service.UserService;
import capstone.jakdu.User.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
//@RequestMapping(value="/v1")
public class UserApiController {
    private final UserService userService;
    private final UserInfoRepository userInfoRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;



    /** * 로그인 * @return */
    @PostMapping("/register/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            System.out.println("hello");
            /** email, password 체크 **/
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
            System.out.println(authRequest);
            System.out.println(token);
            authenticationManager.authenticate(token);

//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
//            );
        } catch (Exception ex) {
            return "fail";
//            ex.printStackTrace();
//            throw new Exception("invalid email or password");
        }
        return jwtUtil.generateToken(authRequest.getEmail());
    }

    /** 회원가입 */
    @PostMapping("/register/register-user")
    public String registUser(@RequestBody UserSaveRequestDto userSaveRequestDto) throws IOException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException {
//        ObjectMapper mapper = new ObjectMapper();
//        System.out.println("userSaveRequestStr = " + userSaveRequestStr);
//        UserSaveRequestDto userSaveRequestDto = mapper.readValue(userSaveRequestStr, UserSaveRequestDto.class);
        userService.userRegister(userSaveRequestDto);
        return "ok";
    }

    /** 이메일 중복검사*/
    @GetMapping("/register/email-check")
    public boolean emailDuplicateCheck(@RequestParam String email) throws IOException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException {
        System.out.println(email);
        if(userInfoRepository.findByEmail(email) == null){
            System.out.println("사용가능한 이메일");
            return true;
        } else{
            System.out.println("사용불가 이메일");
            return false;
        }
    }

    @GetMapping("/test/1")
    public String welcome() {
        return "Welcome to javatechie !!";
    }

    /** * 멤버 조회 * @return */
    @GetMapping("/test/findAll")
    public List<User> findAllMember() {
        return userInfoRepository.findAll();
    }

    @GetMapping("/test/findusername")
    public User findUser(@RequestBody AuthRequest authRequest) throws IOException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException{
        return userInfoRepository.findByEmail(authRequest.getEmail());
    }

    @PostMapping("/test/test2")
    public String test2() {
        System.out.println("welcome");
        return "ok";
    }

//    /* 유저 등록 */
//    @PostMapping("/user/")
//    public List<User> registerUser(@RequestParam("userSaveRequestDto") String userSaveRequestStr) throws IOException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException {
//        ObjectMapper mapper = new ObjectMapper();
//        System.out.println("userSaveRequestStr = " + userSaveRequestStr);
//        UserSaveRequestDto userSaveRequestDto = mapper.readValue(userSaveRequestStr, UserSaveRequestDto.class);
//        userService.userRegister(userSaveRequestDto);
//        return userInfoRepository.findAll();
//    }



//
//   @PostMapping("/user/test")
//    public Long save(@RequestBody UserSaveRequestDto requestDto) {
//       return userService.save(requestDto);
//   }
//   @PutMapping("/user/{id}")
//    public Long update(@PathVariable Long id, @RequestBody UserUpdateRequestDto requestDto){
//       return userService.update(id, requestDto);
//   }
//    @GetMapping("/user/{id}")
//    public UserResponseDto findById (@PathVariable Long id) {
//
//        return userService.findById(id);
//    }


}
