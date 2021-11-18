package capstone.jakdu.Book.encryption;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AES256KeyGenerator {
    private final KeyGenerator keyGenerator;
    private final int keyLength = 32;
    private final int ivLength = 16;

    /**
     * @return random 32byte array (for AES256 key)
     */
    public byte[] generateKey() {
        return keyGenerator.generate(keyLength);
    }

    /**
     * @return random 16byte array (for IV)
     */
    public byte[] generateIv() {
        return keyGenerator.generate(ivLength);
    }
}
