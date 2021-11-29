package capstone.jakdu.Book.controller;

import capstone.jakdu.Book.domain.PDFBook;
import capstone.jakdu.Book.domain.PDFKey;
import capstone.jakdu.Book.domain.PurchasedPageList;
import capstone.jakdu.Book.object.dto.PDFKeyDto;
import capstone.jakdu.Book.repository.PDFBookRepository;
import capstone.jakdu.Book.repository.PDFKeyRepository;
import capstone.jakdu.Common.response.ResponseDto;
import capstone.jakdu.Common.response.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/key")
@RequiredArgsConstructor
public class BookKeyController {
//  유저 추가되면 추가 필요!
    private final PDFKeyRepository pdfKeyRepository;
    private final PDFBookRepository pdfBookRepository;

    @GetMapping("/pdf_test")
    public ResponseDto pdfKeyTest(@RequestParam("book_id") Long id) {
        PDFBook pdfBook = pdfBookRepository.findById(id).get();
        // pdf page
        List<Boolean> userPageList = new ArrayList<>();
        List<Integer> boughtPageList = new ArrayList<>();
        final int startPage = pdfBook.getRealStartPage();
        final int endPage = 178;

        for (int i = 0; i < endPage; i++) {
            userPageList.add(true);
            // 여기서 true들어가면 해당 페이지 키 포함. 아니면 x
//            int test = i % 10;
//            if(test == 7 || test == 8 || test == 9) {
//                userPageList.add(false);
//            }
//            else{
//                userPageList.add(true);
//            }
        }
        PurchasedPageList purchasedPageList = PurchasedPageList.builder()
                .pageList(userPageList)
                .pdfBook(pdfBook)
                .build();

        userPageList = purchasedPageList.getPageList();
        for (int i = startPage - 1; i < endPage; i++) {
            int test = i % 10;
            if(userPageList.get(i) && test != 7 && test != 8 && test != 9) {
                boughtPageList.add(i);
            }
        }

        List<PDFKey> keys = pdfKeyRepository.findAllByPdfBookIdAndPageNumInOrderByPageNumAsc(id, boughtPageList);
        List<PDFKeyDto> keyDtos = new ArrayList<>();
        keys.forEach(key -> {
            keyDtos.add(new PDFKeyDto(
                    key.getPageNum(),
                    key.getDecKey().getBytes(),
                    key.getDecIv().getBytes()));
        });

        //        final int endPage = pdfBook.getEndPage();


        return new ResponseDto(StatusEnum.OK, "success", keyDtos);
    }
}
