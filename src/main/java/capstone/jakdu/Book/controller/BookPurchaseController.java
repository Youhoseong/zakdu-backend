package capstone.jakdu.Book.controller;


import capstone.jakdu.Book.object.HierarchyObject;
import capstone.jakdu.Book.object.dto.PdfBookPurchaseDto;
import capstone.jakdu.Book.object.dto.BookResponseDto;
import capstone.jakdu.Book.object.dto.PurchasePageResDto;
import capstone.jakdu.Book.service.BookPurchaseService;
import capstone.jakdu.Common.response.ResponseDto;
import capstone.jakdu.Common.response.StatusEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseDto pdfBookPurchase(Authentication authentication, @PathVariable Long bookId, @RequestParam String bookPurchaseStr) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        PdfBookPurchaseDto bookPurchaseDto = mapper.readValue(bookPurchaseStr, PdfBookPurchaseDto.class);

        bookPurchaseService.pdfPurchase(bookId, bookPurchaseDto, authentication.getName());
        return new ResponseDto(StatusEnum.OK, "success", null);
    }

    // bookId와 유저 정보로 유저가 구입한 책 페이지을 리턴하는 컨트롤러
    @GetMapping("/info/page/{bookId}")
    public ResponseDto pdfBookPageInformation(@PathVariable Long bookId, Authentication authentication) {
        PurchasePageResDto purchasePageInfo = bookPurchaseService.getPurchasePageInfo(bookId, authentication.getName());
        return new ResponseDto(StatusEnum.OK, "success", purchasePageInfo);
    }



}
