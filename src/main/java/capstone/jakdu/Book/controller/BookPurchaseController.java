package capstone.jakdu.Book.controller;


import capstone.jakdu.Book.domain.PDFBook;
import capstone.jakdu.Book.object.HierarchyObject;
import capstone.jakdu.Book.object.dto.BookPurchaseDto;
import capstone.jakdu.Book.object.dto.BookResponseDto;
import capstone.jakdu.Book.object.dto.PDFBookTocAnalyzeDto;
import capstone.jakdu.Book.service.BookPurchaseService;
import capstone.jakdu.Common.response.ResponseDto;
import capstone.jakdu.Common.response.StatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="book-purchase")
public class BookPurchaseController {

    private final BookPurchaseService bookPurchaseService;


    @GetMapping("/book-list")
    public ResponseDto bookList() throws IOException {
        List<BookResponseDto> bookResponseDtoList = bookPurchaseService.findAllPDFBook();
        return new ResponseDto(StatusEnum.OK, "success", bookResponseDtoList);
    }
    @PostMapping("/test")
    public String test2() {
        System.out.println("welcome");
        return "ok";
    }

    @GetMapping("/book-toc/{bookId}")
    public ResponseDto bookTocList(@PathVariable Long bookId) {
        List<HierarchyObject> hierarchyObjects = bookPurchaseService.findAllPDFTocByBookId(bookId);
        return new ResponseDto(StatusEnum.OK, "success", hierarchyObjects);
    }

    @PostMapping("/pdf-book/{bookId}")
    public ResponseDto pdfBookPurchase(@PathVariable Long bookId, @RequestParam String bookPurchaseStr) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("bookPurchaseDto = " + bookPurchaseStr);
        BookPurchaseDto bookPurchaseDto = mapper.readValue(bookPurchaseStr, BookPurchaseDto.class);
       //System.out.println("bookPurchaseDto = " + bookPurchaseDto);
        //System.out.println("bookPurchaseStr.getBookToc() = " + bookPurchaseStr.getBookToc());
        return new ResponseDto(StatusEnum.OK, "success", null);
    }


}
