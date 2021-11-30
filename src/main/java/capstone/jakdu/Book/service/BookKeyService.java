package capstone.jakdu.Book.service;

import capstone.jakdu.Book.domain.PDFBook;
import capstone.jakdu.Book.domain.PDFKey;
import capstone.jakdu.Book.domain.PurchasedPageList;
import capstone.jakdu.Book.object.dto.PDFKeyDto;
import capstone.jakdu.Book.repository.PDFBookRepository;
import capstone.jakdu.Book.repository.PDFKeyRepository;
import capstone.jakdu.Book.repository.PurchasedPageListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookKeyService {

    private final PurchasedPageListRepository purchasedPageListRepository;
    private final PDFKeyRepository pdfKeyRepository;
    private final PDFBookRepository pdfBookRepository;

    public List<PDFKeyDto> purchasedPdfKeys(Long bookId, Long userId) {

        PDFBook pdfBook = pdfBookRepository.findById(bookId).orElseThrow(NoSuchElementException::new);
        PurchasedPageList purchasedPageList = purchasedPageListRepository.findByPdfBookIdAndUserId(bookId, userId)
                .orElseThrow(NoSuchElementException::new);

        List<Boolean> pageList = purchasedPageList.getPageList();
        List<Integer> purchasedPageNumbers = new ArrayList<>();
        for (int i = 0; i < pageList.size(); i++) {
            if(pageList.get(i)) {
                purchasedPageNumbers.add(i);
            }
        }

        List<PDFKey> keys = pdfKeyRepository.findAllByPdfBookIdAndPageNumInOrderByPageNumAsc(
                bookId,
                purchasedPageNumbers
        );

        List<PDFKeyDto> keyDtos = new ArrayList<>();
        keys.forEach(key -> {
            keyDtos.add(new PDFKeyDto(
                    key.getPageNum(),
                    key.getDecKey().getBytes(),
                    key.getDecIv().getBytes()));
        });

        return keyDtos;
    }
}
