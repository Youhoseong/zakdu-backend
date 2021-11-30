package capstone.jakdu.Book.controller;

import capstone.jakdu.Book.object.HierarchyObject;
import capstone.jakdu.Book.object.dto.BookRegisterDto;
import capstone.jakdu.Book.object.dto.PDFBookTocAnalyzeDto;
import capstone.jakdu.Book.service.BookRegisterService;
import capstone.jakdu.Common.response.ResponseDto;
import capstone.jakdu.Common.response.StatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="/book")
public class BookRegisterController {
    private final BookRegisterService bookRegisterService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/bookmark-analyze")
    public ResponseDto tocAnalyzeWithBookMark(@RequestParam("files") MultipartFile file) throws IOException {
        System.out.println("file = " + file.getOriginalFilename());
        List<HierarchyObject> hierarchyObjects = bookRegisterService.bookmarkAnalysisFromPdf(file);

        if(hierarchyObjects.size() == 0) {
            return new ResponseDto(StatusEnum.BOOKMARK_NO_EXIST, "success", null);
        } else {
            return new ResponseDto(StatusEnum.OK, "success", hierarchyObjects);
        }
    }

    @PostMapping("/zakdu-analyze")
    public ResponseDto tocAnalyzeWithZakduAlgorithm(@RequestParam("bookTocAnalyzeDto") String bookTocAnalyzeStr, @RequestParam("files") MultipartFile files) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        PDFBookTocAnalyzeDto bookTocAnalyzeDto = mapper.readValue(bookTocAnalyzeStr, PDFBookTocAnalyzeDto.class);
        List<HierarchyObject> hierarchyObjects = bookRegisterService.zakduAnalysisFromPdf(files, bookTocAnalyzeDto);

        return new ResponseDto(StatusEnum.OK, "success", hierarchyObjects);
    }


    @PostMapping("/test2")
    public void registerBook(@RequestParam("bookRegisterDto") String bookRegisterStr,
                     @RequestParam("bookFile") MultipartFile bookFile, 
                     @RequestParam("bookCover") MultipartFile bookCover) throws IOException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("bookRegisterStr = " + bookRegisterStr);
        BookRegisterDto bookRegisterDto = mapper.readValue(bookRegisterStr, BookRegisterDto.class);

        System.out.println("bookRegisterDto.getName() = " + bookRegisterDto.getName());
        System.out.println("bookFile.getOriginalFilename() = " + bookFile.getOriginalFilename());
        System.out.println("bookCover.getOriginalFilename() = " + bookCover.getOriginalFilename());

        bookRegisterService.pdfBookRegister(bookRegisterDto, bookFile, bookCover);
    }



}
