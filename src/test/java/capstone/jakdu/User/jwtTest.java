//package capstone.jakdu.User;
//
//import org.junit.jupiter.api.Test;
//
//import javax.websocket.ClientEndpointConfig;
//import java.util.Base64;
//import java.util.Date;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtBuilder;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.stereotype.Service;
//import javax.crypto.spec.SecretKeySpec;
//import javax.xml.bind.DatatypeConverter;
//import java.security.Key;
//import java.util.Date;
//
//public class jwtTest {
//
//    private static final ClientEndpointConfig.Builder JWT = ;
//
//    @Test
//    public void jwtTockenTest() {
//        // Create JWT Token
//        String token = JWT.create()
//                .withSubject("helloworld")
//                .withExpiresAt(new Date(System.currentTimeMillis() + 864000000))
//                .sign(Algorithm.HMAC512("test".getBytes()));
//
//        //jwt token header.payload.secret
//        System.out.println(token);
//
//        Base64.Decoder decoder = Base64.getDecoder();
//        byte[] decodedBytes = decoder.decode("eyJzdWIiOiJoZWxsb3dvcmxkIiwiZXhwIjoxNTk0NTQ5MzAxfQ".getBytes());
//
//        //payload
//        System.out.println(new String(decodedBytes));
//
//        //검증
//        try {
//            String result = JWT.require(Algorithm.HMAC512("test".getBytes()))
//                    .build()
//                    .verify(token.replace("Bearer", ""))
//                    .getSubject();
//
//            //result
//            System.out.println(result);
//        }catch(Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//}