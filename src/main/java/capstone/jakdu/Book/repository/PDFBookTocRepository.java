package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.PDFBookToc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PDFBookTocRepository extends JpaRepository<PDFBookToc, Long> {
    Optional<PDFBookToc> findById(Long id);
}
