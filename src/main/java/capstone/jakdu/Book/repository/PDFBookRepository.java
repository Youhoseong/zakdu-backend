package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.PDFBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PDFBookRepository extends JpaRepository<PDFBook, Long> {
}
