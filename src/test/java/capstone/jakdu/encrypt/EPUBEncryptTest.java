package capstone.jakdu.encrypt;

import capstone.jakdu.Book.encryption.AESEncrypt;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;

@SpringBootTest
public class EPUBEncryptTest {

    private final String alg = "AES/CBC/PKCS5Padding";
    private final String aesKey = "abcdefghijklmnopqrstuvwxyzabcdef";
    private final String aesIv = "0123456789abcdef";
    @Autowired
    private AESEncrypt encryptor;
    private final String hiddenDivPrefix = "<div class= \"class\" name=\"name\" id=\"id\" style=\"display:none\">";
    private final String hiddenDivPostfix = "</div>";

    public byte[] encrypt(byte[] bytes, String alg, String aesKey, String aesIv) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");
        // iv로 spec 생성
        IvParameterSpec ivParameterSpec = new IvParameterSpec(aesIv.getBytes());
        // 암호화 적용
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

        return cipher.doFinal(bytes);
    }

    public byte[] decrypt(byte[] bytes, String alg, String aesKey, String aesIv) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(aesIv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        return cipher.doFinal(bytes);
    }

    @Test
    public void EPUB_내부_파일_목록() throws IOException {

        String fileName = "./이펍/" + "dec.epub";
        EpubReader epubReader = new EpubReader();
        Book book = epubReader.readEpub(new FileInputStream(fileName));
        // /OEBPS/Cath_9780553418828_epub3_itr_r1.xhtml
        book.getContents().forEach(content -> {
            System.out.println("content.getHref() = " + content.getHref());
        });
        Resource resource = book.getContents().get(7);
        System.out.println("resource.getHref() = " + resource.getHref());
        System.out.println("resource.getInputEncoding() = " + resource.getInputEncoding());
        String s = new String(resource.getData(), StandardCharsets.UTF_8);
        String s1 = Arrays.toString(resource.getData());
        System.out.println("s = " + s);
        System.out.println("s1 = " + s1);
    }

    @Test
    public void EPUB_복호화_테스트() throws Exception {

        final String fileName = "Weapons of Math Destruction How Big Data Increases Inequality and Threatens Democracy by Cathy O’Neil (z-lib.org)_enc";
        final String fileExtension = ".epub";
        final String filePath = "./이펍/" + fileName + fileExtension;
        final String encFilePath = "./이펍/" + fileName + "_dec" + fileExtension;
        Files.copy(Paths.get(filePath), Paths.get(encFilePath));

        Path zipFilePath = Paths.get(encFilePath);

        try( FileSystem fs = FileSystems.newFileSystem(zipFilePath, (ClassLoader) null)){
            Path fileInsideZipPath = fs.getPath("/OEBPS/Cath_9780553418828_epub3_itr_r1.xhtml");
            InputStream inputStream = Files.newInputStream(fileInsideZipPath);
            byte[] bytes = inputStream.readAllBytes();
            byte[] encryptedData = decrypt(bytes, alg, aesKey, aesIv);
            Files.write(fileInsideZipPath, encryptedData, StandardOpenOption.WRITE);

            System.out.println("bytes = " + new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void EPUB_파일_저장_테스트() throws Exception {
        String fileName = "./이펍/" + "Weapons of Math Destruction How Big Data Increases Inequality and Threatens Democracy by Cathy O’Neil (z-lib.org).epub";
        EpubReader epubReader = new EpubReader();
        Book book = epubReader.readEpub(new FileInputStream(fileName));

        EpubWriter epubWriter = new EpubWriter();
        epubWriter.write(book, new FileOutputStream("./이펍/" + "Weapons of Math Destruction How Big Data Increases Inequality and Threatens Democracy by Cathy O’Neil (z-lib.org)_copy.epub"));
    }

    @Test
    public void EPUB_암호화_테스트() throws Exception{
        final String fileName = "Weapons of Math Destruction How Big Data Increases Inequality and Threatens Democracy by Cathy O’Neil (z-lib.org)";
        final String fileExtension = ".epub";
        final String filePath = "./이펍/" + fileName + fileExtension;
        final String encFilePath = "./이펍/" + fileName + "_enc" + fileExtension;
        Files.copy(Paths.get(filePath), Paths.get(encFilePath));

        Path zipFilePath = Paths.get(encFilePath);

        try( FileSystem fs = FileSystems.newFileSystem(zipFilePath, (ClassLoader) null)){
            Path fileInsideZipPath = fs.getPath("/OEBPS/Cath_9780553418828_epub3_itr_r1.xhtml");
            InputStream inputStream = Files.newInputStream(fileInsideZipPath);
            byte[] bytes = inputStream.readAllBytes();
            String encrypted = encryptor.encrypt(bytes, aesKey.getBytes(StandardCharsets.UTF_8), aesIv.getBytes(StandardCharsets.UTF_8));
            Files.write(fileInsideZipPath, encrypted.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE);
            System.out.println("fileInsideZipPath = " + fileInsideZipPath);
            System.out.println("origin = " + new String(bytes, StandardCharsets.UTF_8));
            System.out.println("encrypted = " + new String(encrypted.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
