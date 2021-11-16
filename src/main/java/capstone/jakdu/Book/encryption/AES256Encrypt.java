package capstone.jakdu.Book.encryption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AES256Encrypt implements AESEncrypt {
    private final Cipher cipher;
    private final String alg = "AES";

    @Override
    public byte[] encrypt(byte[] bytes, byte[] key, byte[] iv) throws IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec keySpec = new SecretKeySpec(key, alg);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        try {
            cipher.init(Cipher.ENCRYPT_MODE ,keySpec, ivParameterSpec);
        }
        catch(InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
        catch(InvalidKeyException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }

        return cipher.doFinal(bytes);
    }
}
