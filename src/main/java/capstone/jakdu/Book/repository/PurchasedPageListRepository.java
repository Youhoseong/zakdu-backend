package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.PurchasedPageList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchasedPageListRepository extends JpaRepository<PurchasedPageList, Long> {

    Optional<PurchasedPageList> findByPdfBookIdAndUserId(Long pdfBookId, Long userId);
    Boolean existsByPdfBookIdAndUserId(Long pdfBookId, Long userId);

}
