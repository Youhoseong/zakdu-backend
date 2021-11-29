package capstone.jakdu.Book.service;

import capstone.jakdu.Book.domain.FileStream;
import capstone.jakdu.Book.domain.PDFBook;
import capstone.jakdu.Book.object.dto.BookDownloadDto;
import capstone.jakdu.Book.repository.PDFBookRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
@RequiredArgsConstructor
public class BookDownloadService {

    private final PDFBookRepository pdfBookRepository;
    private final PDFBookEncryptService pdfBookEncryptService;

    public BookDownloadDto downloadBook(Long bookId) throws IOException {
        PDFBook pdfBook = pdfBookRepository.findById(bookId).get();

        FileStream bookFile = pdfBook.getBookFile();

        String filePath = pdfBookEncryptService.getEncPdfPath();
        String fileName = bookFile.getFileName();
        String coverFileName = pdfBook.getBookCover().getFileName();

        File pdfBookFile = new File(filePath + fileName);
        Path path = Paths.get(pdfBookFile.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return new BookDownloadDto(bookId, fileName, coverFileName, resource.getByteArray());
    }

    public byte[] downloadLockPdf() throws IOException {
        final String filePath = System.getProperty("user.dir") + "/lockfile/" + "lockpage.pdf";
        File lockPdfFile = new File(filePath);
        Path path = Paths.get(lockPdfFile.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        return resource.getByteArray();
    }
}
