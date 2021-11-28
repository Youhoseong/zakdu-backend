package capstone.jakdu.Book.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EPUBBookEncryptServiceTest {
    @Autowired
    private EPUBBookEncryptService epubBookEncryptService;
    private final String encEpubPath = System.getProperty("user.dir") + "/encEpubBook/";

    @Test
    public void epubEncryptTest() throws IllegalBlockSizeException, IOException, BadPaddingException {
        epubBookEncryptService.encryptEpubBook(1L);
    }

    @Test
    public void epubDecryptTest() {
        String encFileName = "example.epub";
        String decFileName = "example_dec.epub";


    }
}