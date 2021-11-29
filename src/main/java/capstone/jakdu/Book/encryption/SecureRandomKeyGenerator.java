package capstone.jakdu.Book.encryption;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

@Component
public class SecureRandomKeyGenerator implements KeyGenerator {
    private final SecureRandom sr = new SecureRandom();

//    public byte[] generate(int length) {
//        if(length < 1) throw new IllegalArgumentException("length must be positive integer");
//        byte[] bytes = new byte[length];
//        sr.nextBytes(bytes);
//        for (int i = 0; i < bytes.length; i++) {
//            if(bytes[i] < 0) bytes[i] = (byte) (bytes[i] + 128);
//        }
//        return bytes;
//    }
    // 이전에 사용하던 키 생성 알고리즘, 쓸 지 모르겠음

    public byte[] generate(int length) {
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z', '!', '@', '#', '$', '%', '^', '&' };

        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();
        int idx = 0;
        int len = charSet.length;
        for (int i=0; i<length; i++) {
            idx = sr.nextInt(len);
            sb.append(charSet[idx]); }
        return sb.toString().getBytes(StandardCharsets.US_ASCII);

    }
}
