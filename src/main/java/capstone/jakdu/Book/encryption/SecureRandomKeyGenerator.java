package capstone.jakdu.Book.encryption;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Arrays;

@Component
public class SecureRandomKeyGenerator implements KeyGenerator {
    private final SecureRandom sr = new SecureRandom();

    public byte[] generate(int length) {
        if(length < 1) throw new IllegalArgumentException("length must be positive integer");
        byte[] bytes = new byte[length];
        sr.nextBytes(bytes);
        return bytes;
    }
}
