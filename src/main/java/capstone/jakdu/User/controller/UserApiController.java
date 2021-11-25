package capstone.jakdu.User.controller;

import capstone.jakdu.User.domain.AuthRequest;
import capstone.jakdu.User.domain.User;
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
@RestController
//@RequestMapping(value="/v1")
public class UserApiController {
    private final UserService userService;
    private final UserInfoRepository userInfoRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;


    @GetMapping("/")
    public String welcome() {
        return "Welcome to javatechie !!";
    }

    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            System.out.println("hello");
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
            System.out.println(authRequest);
            System.out.println(token);
            authenticationManager.authenticate(token);

//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
//            );
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("inavalid username/password");
        }
        return jwtUtil.generateToken(authRequest.getUsername());
    }




    /** * 멤버 조회 * @return */
    @GetMapping("test/findAll")
    public List<User> findAllMember() {
        return userInfoRepository.findAll();
    }
    /** * 회원가입 * @return */
//    @PostMapping("test")
//    public User signUp() {
//        final User member = User.builder()
//                .email("test_user@gmail.com")
//                .username("test user")
//                .userType("customer")
//                .password("123")
//                .point(Long.valueOf(1000))
//                .build();
//        return userInfoRepository.save(member);
//    }
    /* 유저 등록 */
    @PostMapping("/user")
    public List<User> registerUser(@RequestParam("userSaveRequestDto") String userSaveRequestStr) throws IOException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("userSaveRequestStr = " + userSaveRequestStr);
        UserSaveRequestDto userSaveRequestDto = mapper.readValue(userSaveRequestStr, UserSaveRequestDto.class);
        userService.userRegister(userSaveRequestDto);
        return userInfoRepository.findAll();
    }
    /* 유저 등록 */
    @PostMapping("/user/1")
    public String registUser(@RequestBody UserSaveRequestDto userSaveRequestDto) throws IOException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException {
//        ObjectMapper mapper = new ObjectMapper();
//        System.out.println("userSaveRequestStr = " + userSaveRequestStr);
//        UserSaveRequestDto userSaveRequestDto = mapper.readValue(userSaveRequestStr, UserSaveRequestDto.class);
        userService.userRegister(userSaveRequestDto);
        return "ok";
    }

    @GetMapping("/test/findusername")
    public User findUser(@RequestBody AuthRequest authRequest) {
        return userInfoRepository.findByUsername(authRequest.getUsername());
    }



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

    @PostMapping("/test2")
    public String test2() {
        System.out.println("welcome");
        return "ok";
    }

}
