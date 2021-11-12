package capstone.jakdu.Book.controller;

import capstone.jakdu.Book.object.HierarchyObject;
import capstone.jakdu.Book.object.dto.PDFBookTocAnalyzeDto;
import capstone.jakdu.Book.service.BookRegisterService;
import capstone.jakdu.Common.response.ResponseDto;
import capstone.jakdu.Common.response.StatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        System.out.println("bookTocAnalyzeDto.getBookPDFRowCount() = " + bookTocAnalyzeDto.getBookPDFRowCount());
        System.out.println("bookTocAnalyzeDto.getBookPDFTocEndPage() = " + bookTocAnalyzeDto.getBookPDFTocEndPage());
        System.out.println("bookTocAnalyzeDto.getBookPDFTocStartPage() = " + bookTocAnalyzeDto.getBookPDFTocStartPage());
        System.out.println("files.getOriginalFilename() = " + files.getOriginalFilename());


        List<HierarchyObject> hierarchyObjects = bookRegisterService.zakduAnalysisFromPdf(files, bookTocAnalyzeDto);

        return new ResponseDto(StatusEnum.OK, "success", hierarchyObjects);
    }

    @PostMapping("/text3")
    public void test3(@RequestParam("files")String bookTocAnalyzeDto) {
        System.out.println("bookTocAnalyzeDto = " + bookTocAnalyzeDto);

    }

    @PostMapping("/test")
    public String test2() {
        System.out.println("welcome");
        return "ok";
    }




}
