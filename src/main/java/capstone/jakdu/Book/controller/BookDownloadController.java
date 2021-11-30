package capstone.jakdu.Book.controller;

import capstone.jakdu.Book.object.dto.BookFileNameDto;
import capstone.jakdu.Book.service.BookDownloadService;
import capstone.jakdu.Book.service.EPUBBookEncryptService;
import capstone.jakdu.Common.response.ResponseDto;
import capstone.jakdu.Common.response.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class BookDownloadController {

    private final BookDownloadService bookDownloadService;
    private final EPUBBookEncryptService epubBookEncryptService;

    @GetMapping("/pdf")
    public ResponseDto getFileNames(@RequestParam("id") Long id) throws IOException {
        // 유저 구매여부 확인, 유저 정보도 추가로 받아야 함
        BookFileNameDto bookDownloadDto = bookDownloadService.downloadBook(id);

        if(bookDownloadDto == null) {
            return new ResponseDto(StatusEnum.INTERNAL_SERVER_ERROR, "internal server error", null);
        }
        else {
            return new ResponseDto(StatusEnum.OK, "success", bookDownloadDto);
        }
    }

    @GetMapping("/pdf-2")
    public ResponseEntity<InputStreamResource> downloadPdfBook2(@RequestParam("id") Long id) throws FileNotFoundException {
        return bookDownloadService.downloadPdfBook(id);
    }

    @GetMapping("/pdf_lock")
    public ResponseDto downloadLockPdf() throws IOException {
        byte[] lockPdf = bookDownloadService.downloadLockPdf();
        return new ResponseDto(StatusEnum.OK, "success", lockPdf);
    }

    @GetMapping("/epub-test")
    public ResponseDto epubtest() throws IllegalBlockSizeException, IOException, BadPaddingException {
        epubBookEncryptService.encryptEpubBook(1L);
        return new ResponseDto(StatusEnum.OK, "success", null);
    }
}
