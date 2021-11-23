package capstone.jakdu.encrypt;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;

public class PDFEncryptTest {
    //PKCS7이 적용됨 프론트에선 PKCS7모드 사용해야 할듯?
    private final String alg = "AES/CBC/PKCS5Padding";
    private final String aesKey = "abcdefghijklmnopqrstuvwxyzabcdef";
    private final String aesIv = "0123456789abcdef";
    //NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, UnsupportedEncodingException
    public String encrypt(String str) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        // key로 비밀 키 생성
        System.out.println("Arrays.toString(aesKey.getBytes()) = " + Arrays.toString(aesKey.getBytes()));
        SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");
        // iv로 spec 생성
        IvParameterSpec ivParameterSpec = new IvParameterSpec(aesIv.getBytes());
        // 암호화 적용
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

        byte[] encrypted = cipher.doFinal(str.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String str) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(aesKey.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(aesIv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] decoded = Base64.getDecoder().decode(str);
        byte[] decrypted = cipher.doFinal(decoded);
        String s = new String(decrypted);
        return s;
    }

    public String decrypt(String str, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] decoded = Base64.getDecoder().decode(str);
        byte[] decrypted = cipher.doFinal(decoded);
        String s = new String(decrypted);
        return s;
    }

    @Test
    public void 암호화_복호화_테스트() throws Exception {
        String s = "암호화 복호화 테스트 문자열";
        System.out.println("s = " + s);
        String encrypted = encrypt(s);
        System.out.println("encrypted = " + encrypted);

        String decrypt = decrypt(encrypted);
        System.out.println("decrypt = " + decrypt);
    }

    @Test
    public void 암호화된_PDF_복호화() throws Exception {
        String fileName = "./v";
        int page = 0;
        File source = new File(fileName);

        PDDocument pdfDoc = PDDocument.load(source);
        PDPage pdfDocPage = pdfDoc.getPage(page);
        Iterator<PDStream> streamIterator = pdfDocPage.getContentStreams();

        while(streamIterator.hasNext()) {

            PDStream stream = streamIterator.next();
            String s = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            System.out.println("s = " + s);

            String decrypt = decrypt(s);
            InputStream inputStream = new ByteArrayInputStream(decrypt.getBytes());
            PDStream decryptedStream = new PDStream(pdfDoc, inputStream);
            pdfDocPage.setContents(decryptedStream);

            System.out.println("decrypt = " + decrypt);
        }
        pdfDoc.save("9종교과서시크릿수학1-본문(학생용)_dec.pdf");
        pdfDoc.close();
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
            String encrypt = encrypt(new String(stream.toByteArray()));
            InputStream inputStream = new ByteArrayInputStream(encrypt.getBytes());
            PDStream newStream = new PDStream(pdfDoc, inputStream);

            pdfDocPage.setContents(newStream);
            System.out.println("original = " + new String(stream.toByteArray()));
            System.out.println("encrypt = " + encrypt);
            String s = new String(stream.toByteArray(), StandardCharsets.UTF_8);
        }
        pdfDoc.save("./피디에프/문제집/example_enc.pdf");
        pdfDoc.close();
    }

    @Test
    public void 원본_복호화_내용_확인() throws Exception {
        String fileNameOrigin = "9종교과서시크릿수학1-본문(학생용).pdf";
        String fileNameDec = "9종교과서시크릿수학1-본문(학생용)_dec.pdf";
        int page = 0;

        File fileOrigin = new File(fileNameOrigin);
        File fileDec = new File(fileNameDec);

        PDDocument originDoc = PDDocument.load(fileOrigin);
        PDDocument decDoc = PDDocument.load(fileDec);

        PDPage originPage = originDoc.getPage(page);
        PDPage decPage = decDoc.getPage(page);

        Assertions.assertEquals(new String(originPage.getContentStreams().next().toByteArray()),
                new String(decPage.getContentStreams().next().toByteArray()));
    }

    @Test
    public void 바이트_추출() throws Exception {
        String fileName = "example_enc.pdf";
        int page = 1;

        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        PDPage pdfDocPage = pdfDoc.getPage(page);
        Iterator<PDStream> contentStreams = pdfDocPage.getContentStreams();

        while(contentStreams.hasNext()) {
            PDStream stream = contentStreams.next();

            String s = new String(stream.toByteArray(), StandardCharsets.UTF_8);
            System.out.println("s = " + s);
        }

        pdfDoc.close();
    }

    @Test
    public void 여러_페이지_암호화_테스트() throws Exception {
        String fileName = "./피디에프/문제집/example.pdf";
        final int startPage = 1;
        final int endPage = 178;
        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        for (int i = startPage; i < endPage; i++) {
            PDPage pdfDocPage = pdfDoc.getPage(i);
            Iterator<PDStream> contentStreams = pdfDocPage.getContentStreams();
            while(contentStreams.hasNext()) {
                PDStream stream = contentStreams.next();
                String encrypt = encrypt(new String(stream.toByteArray()));
                InputStream inputStream = new ByteArrayInputStream(encrypt.getBytes());
                PDStream newStream = new PDStream(pdfDoc, inputStream);

                pdfDocPage.setContents(newStream);
                System.out.println("original = " + new String(stream.toByteArray()));
                System.out.println("encrypt = " + encrypt);
                String s = new String(stream.toByteArray(), StandardCharsets.UTF_8);
                System.out.println("s = " + s);
            }

        }
        pdfDoc.save("./피디에프/문제집/example_enc.pdf");
        pdfDoc.close();
    }

    @Test
    public void 여러_페이지_복호화_테스트() throws Exception {
        String fileName = "./피디에프/문제집/example_enc2.pdf";
        final int startPage = 1;
        final int endPage = 17;
        File source = new File(fileName);
        PDDocument pdfDoc = PDDocument.load(source);
        for (int i = startPage; i < endPage; i++) {
            PDPage pdfDocPage = pdfDoc.getPage(i);
            Iterator<PDStream> contentStreams = pdfDocPage.getContentStreams();
            while(contentStreams.hasNext()) {

                PDStream stream = contentStreams.next();
                String s = new String(stream.toByteArray());
                System.out.println("s = " + s);

                String decrypt = decrypt(s);
                InputStream inputStream = new ByteArrayInputStream(decrypt.getBytes());
                PDStream decryptedStream = new PDStream(pdfDoc, inputStream);
                pdfDocPage.setContents(decryptedStream);

                System.out.println("decrypt = " + decrypt);
            }

        }
        pdfDoc.save("./피디에프/문제집/example_dec2.pdf");
        pdfDoc.close();
    }
}
