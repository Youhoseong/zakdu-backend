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
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class AES256Encrypt implements AESEncrypt {
    private final Cipher cipher;
    private final String alg = "AES";

    @Override
    public String encrypt(byte[] bytes, byte[] key, byte[] iv) throws IllegalBlockSizeException, BadPaddingException {
//        Cipher cipher = null;
//        try {
//            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            return null;
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//            return null;
//        }
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

        byte[] encryptedBytes = cipher.doFinal(bytes);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
