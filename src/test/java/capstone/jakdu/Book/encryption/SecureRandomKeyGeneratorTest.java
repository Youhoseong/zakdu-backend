package capstone.jakdu.Book.encryption;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

@SpringBootTest
class SecureRandomKeyGeneratorTest {
    //private SecureRandomKeyGenerator keyGenerator = new SecureRandomKeyGenerator();
    @Autowired
    private AESEncrypt encryptor;
    @Autowired
    private AES256KeyGenerator keyGenerator;

    @Test
    public void 키_생성_테스트() {
        for(int i = 0; i < 1000; i++) {
            byte[] s = keyGenerator.generateKey();
            String s1 = new String(s, StandardCharsets.UTF_8);
            System.out.println("s = " + Arrays.toString(s) + " " + s1);
            System.out.println("s1= " + Arrays.toString(s1.getBytes(StandardCharsets.UTF_8)));
            Assertions.assertEquals(Arrays.toString(s), Arrays.toString(s1.getBytes(StandardCharsets.UTF_8)));
        }
    }

    public String decrypt(byte[] str, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        //byte[] decoded = Base64.getDecoder().decode(str);
        byte[] decrypted = cipher.doFinal(str);
        String s = new String(decrypted);
        return s;
    }

    @Test
    public void PDF_특정_페이지_암호화() throws Exception {
        String fileName = "./피디에프/문제집/example.pdf";
        int page = 2;

        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        PDPage pdfDocPage = pdfDoc.getPage(page);
        Iterator<PDStream> contentStreams = pdfDocPage.getContentStreams();
        while(contentStreams.hasNext()) {
            PDStream stream = contentStreams.next();
            byte[] key = keyGenerator.generateKey();
            byte[] iv = keyGenerator.generateIv();

            System.out.println("encrypter = " + encryptor.toString());
            byte[] encrypt = encryptor.encrypt(stream.toByteArray(), key, iv);
            String decrypt = decrypt(encrypt, key, iv);
            System.out.println("decrypt = " + decrypt);
            InputStream inputStream = new ByteArrayInputStream(encrypt);
            PDStream newStream = new PDStream(pdfDoc, inputStream);

            pdfDocPage.setContents(newStream);
            //System.out.println("original = " + new String(stream.toByteArray()));
            //System.out.println("encrypt = " + encrypt);
            String s = new String(stream.toByteArray(), StandardCharsets.UTF_8);
        }
        pdfDoc.save("./피디에프/문제집/example_enc.pdf");
        pdfDoc.close();
    }
}