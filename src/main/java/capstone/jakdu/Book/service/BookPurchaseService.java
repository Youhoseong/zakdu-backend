package capstone.jakdu.Book.service;


import capstone.jakdu.Book.domain.FileStream;
import capstone.jakdu.Book.domain.PDFBook;
import capstone.jakdu.Book.object.dto.BookResponseDto;
import capstone.jakdu.Book.repository.PDFBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookPurchaseService {

    private final PDFBookRepository pdfBookRepository;

    // pdf and epub을 동시에 가져오는 메소드 필요
    public List<BookResponseDto> findAllPDFBook() throws IOException {
        List<PDFBook> pdfBookList = pdfBookRepository.findAll();
        List<BookResponseDto> bookResponseDtoList = new ArrayList<>();
        for(int i=0; i<pdfBookList.size(); i++) {
            FileStream fileStream = pdfBookList.get(i).getBookCover();
            File bookCoverFile = new File(fileStream.getFilePath() + fileStream.getFileName());

            Path path = Paths.get(bookCoverFile.getAbsolutePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            bookResponseDtoList.add(new BookResponseDto(pdfBookList.get(i), resource.getByteArray()));
        }

        return bookResponseDtoList;
    }
}
