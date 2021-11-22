package capstone.jakdu.Book.service;

import capstone.jakdu.Book.domain.FileStream;
import capstone.jakdu.Book.domain.PDFBook;
import capstone.jakdu.Book.object.dto.BookDownloadDto;
import capstone.jakdu.Book.repository.PDFBookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookDownloadService {

    private final PDFBookRepository pdfBookRepository;
    private final PDFBookEncryptService pdfBookEncryptService;

    public BookDownloadDto downloadBook(Long bookId) {
        PDFBook pdfBook = pdfBookRepository.findById(bookId).get();

        FileStream bookFile = pdfBook.getBookFile();

        String filePath = pdfBookEncryptService.getEncPdfPath();
        String fileName = bookFile.getFileName();
        String title = pdfBook.getName();

        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(filePath + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new BookDownloadDto(bookId, title, fileName, bytes);
    }

    public BookDownloadDto downloadBookTest(Long bookId) {
        String filePath = "./피디에프/문제집/";
        String fileName = "example.pdf";

        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(filePath + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new BookDownloadDto(bookId, "test book title",fileName, bytes);
    }
}
