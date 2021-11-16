package capstone.jakdu.Book.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

public interface AESEncrypt {
    byte[] encrypt(byte[] bytes, byte[] key, byte[] iv) throws IllegalBlockSizeException, BadPaddingException;
}
