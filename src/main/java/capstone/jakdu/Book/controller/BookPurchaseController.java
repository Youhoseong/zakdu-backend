package capstone.jakdu.Book.controller;


import capstone.jakdu.Book.domain.PDFBook;
import capstone.jakdu.Book.object.dto.BookResponseDto;
import capstone.jakdu.Book.service.BookPurchaseService;
import capstone.jakdu.Common.response.ResponseDto;
import capstone.jakdu.Common.response.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
