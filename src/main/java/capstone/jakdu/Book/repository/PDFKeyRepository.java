package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.PDFKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PDFKeyRepository extends JpaRepository<PDFKey, Long> {
    Optional<PDFKey> findById(Long id);
    Optional<PDFKey> findByPdfBookIdAndPageNum(Long bookId, int pageNum);
    //List<PDFKey> findAllByBookId(Long bookId);
    List<PDFKey> findAllByPageNumInOrderByPageNumAsc(Collection<Integer> pageNums);
}
