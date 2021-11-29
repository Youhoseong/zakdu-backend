package capstone.jakdu.Book.service;

import capstone.jakdu.Book.domain.FileStream;
import capstone.jakdu.Book.domain.PDFBook;
import capstone.jakdu.Book.object.dto.BookFileNameDto;
import capstone.jakdu.Book.repository.PDFBookRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@RequiredArgsConstructor
public class BookDownloadService {

    private final PDFBookRepository pdfBookRepository;
    private final PDFBookEncryptService pdfBookEncryptService;

    public BookFileNameDto downloadBook(Long bookId) throws IOException {
        PDFBook pdfBook = pdfBookRepository.findById(bookId).get();

        FileStream bookFile = pdfBook.getBookFile();

        String fileName = bookFile.getFileName();
        String coverFileName = pdfBook.getBookCover().getFileName();

        return new BookFileNameDto(bookId, fileName, coverFileName);
    }

    public ResponseEntity<InputStreamResource> downloadPdfBook(Long bookId) throws FileNotFoundException {
        PDFBook pdfBook = pdfBookRepository.findById(bookId).get();

        FileStream bookFile = pdfBook.getBookFile();

        String filePath = pdfBookEncryptService.getEncPdfPath();
        String fileName = bookFile.getFileName();
        File file = new File(filePath + fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

    public byte[] downloadLockPdf() throws IOException {
        final String filePath = System.getProperty("user.dir") + "/lockfile/" + "lockpage.pdf";
        File lockPdfFile = new File(filePath);
        Path path = Paths.get(lockPdfFile.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        return resource.getByteArray();
    }
}
