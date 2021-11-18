package capstone.jakdu.Book.service;

import capstone.jakdu.Book.domain.FileStream;
import capstone.jakdu.Book.domain.PDFBook;
import capstone.jakdu.Book.domain.PDFKey;
import capstone.jakdu.Book.encryption.AES256KeyGenerator;
import capstone.jakdu.Book.encryption.AESEncrypt;
import capstone.jakdu.Book.repository.PDFKeyRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PDFBookEncryptService {

    private final AESEncrypt encryptor;
    private final AES256KeyGenerator keyGenerator;
    private final PDFKeyRepository pdfKeyRepository;
    @Getter
    private final String encPdfPath = System.getProperty("user.dir") + "/encPdfBook/";
    /**
     * @param pdfBook
     *
     * 해당 PDF 파일을 암호화하고 "endPdfBook" 디렉토리에 저장. 파일 이름은 동일
     *
     */
    @Transactional(rollbackOn = Exception.class)
    public void encryptPdfBook(PDFBook pdfBook) throws IOException, IllegalBlockSizeException, BadPaddingException {

        FileStream bookFile = pdfBook.getBookFile();
        final String source = bookFile.getFilePath() + bookFile.getFileName();
        File pdfFile = new File(source);
        PDDocument doc;
        try {
            doc = PDDocument.load(pdfFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException("File : " + source + " does not exist");
        }

        final int encStartPage = pdfBook.getRealStartPage();
        // !
        final int encEndPage = doc.getNumberOfPages() - 1;

        List<PDFKey> pdfKeyList = new ArrayList<>();
        for(int i = encStartPage - 1; i < encEndPage; i++) {
            PDPage pdfDocPage = doc.getPage(i);
            Iterator<PDStream> contentStreams = pdfDocPage.getContentStreams();
            while(contentStreams.hasNext()) {
                PDStream stream = contentStreams.next();
                byte[] bytes = stream.toByteArray();
                byte[] aesKey = keyGenerator.generateKey();
                byte[] aesIv = keyGenerator.generateIv();

                byte[] encrypt = encryptor.encrypt(bytes, aesKey, aesIv);
                InputStream inputStream = new ByteArrayInputStream(encrypt);
                PDStream newStream = new PDStream(doc, inputStream);

                pdfDocPage.setContents(newStream);
                PDFKey pdfKey = PDFKey.builder()
                        .pdfBook(pdfBook)
                        .pageNum(i + 1)
                        .decKey(new String(aesKey, StandardCharsets.US_ASCII))
                        .decIv(new String(aesIv, StandardCharsets.US_ASCII))
                        .build();
                pdfKeyList.add(pdfKey);
            }
        }
        File dir = new File(encPdfPath);
        if(!dir.exists())
            dir.mkdirs();

        doc.save(encPdfPath + bookFile.getFileName());
        doc.close();
        pdfKeyRepository.saveAll(pdfKeyList);
    }
}
