package capstone.jakdu.Book.service;

import capstone.jakdu.Book.domain.EPUBKey;
import capstone.jakdu.Book.domain.EpubFileToc;
import capstone.jakdu.Book.encryption.AES256KeyGenerator;
import capstone.jakdu.Book.encryption.AESEncrypt;
import capstone.jakdu.Book.repository.EPUBKeyRepository;
import capstone.jakdu.Book.repository.EpubFileTocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EPUBBookEncryptService {

    private final EpubFileTocRepository epubFileTocRepository;
    private final EPUBKeyRepository epubKeyRepository;
    private final AESEncrypt encryptor;
    private final AES256KeyGenerator keyGenerator;
    private final String encEpubPath = System.getProperty("user.dir") + "/encEpubBook/";

    @Transactional(rollbackOn = Exception.class)
    public void encryptEpubBook(Long bookId) throws IOException, IllegalBlockSizeException, BadPaddingException {
        // for test
        final String bookPath = System.getProperty("user.dir") + "/epubBook/";
        final String bookName = "example.epub";

        File dir = new File(encEpubPath);
        if(!dir.exists())
            dir.mkdirs();

        Files.copy(Paths.get(bookPath + bookName), Paths.get(encEpubPath + bookName));
        List<EpubFileToc> tocList = epubFileTocRepository.findAllByBookIdOrderByIdDesc(bookId);
        Path path = Paths.get(encEpubPath + bookName);
        System.out.println("path = " + path);
        int prevSellGroup = -1;
        byte[] aesKey = null;
        byte[] aesIv = null;
        ArrayList<EPUBKey> keyList = new ArrayList<>();

        try (FileSystem fs = FileSystems.newFileSystem(path, (ClassLoader) null)) {
            for (int i = 0; i < tocList.size(); i++) {
                EpubFileToc tocFile = tocList.get(i);
                Path fileInsideZipPath = fs.getPath("/" + tocFile.getFileTitle());
                InputStream inputStream = Files.newInputStream(fileInsideZipPath);
                if (prevSellGroup != tocFile.getSellGroup()) {
                    aesKey = keyGenerator.generateKey();
                    aesIv = keyGenerator.generateIv();
                    keyList.add(EPUBKey.builder()
                            .epubBookId(bookId)
                            .sellGroup(tocFile.getSellGroup())
                            .decKey(new String(aesKey, StandardCharsets.UTF_8))
                            .decIv(new String(aesIv, StandardCharsets.UTF_8))
                            .build());
                    prevSellGroup = tocFile.getSellGroup();
                }
                byte[] bytes = inputStream.readAllBytes();
                String encrypted = encryptor.encrypt(bytes, aesKey, aesIv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        epubKeyRepository.saveAll(keyList);
    }
}
