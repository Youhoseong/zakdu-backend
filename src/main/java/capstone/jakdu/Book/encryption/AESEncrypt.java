package capstone.jakdu.Book.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public interface AESEncrypt {
    /**
     * @param bytes 암호화할 내용
     * @param key key
     * @param iv initial vector
     * @return
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    String encrypt(byte[] bytes, byte[] key, byte[] iv) throws IllegalBlockSizeException, BadPaddingException;
}
