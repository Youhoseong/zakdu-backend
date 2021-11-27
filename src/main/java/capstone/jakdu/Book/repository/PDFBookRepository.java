package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.PDFBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PDFBookRepository extends JpaRepository<PDFBook, Long> {


   default PDFBook findByIdOrElseThrow(Long id) {

       return findById(id).orElseThrow(()-> new IllegalArgumentException("해당 책이 없습니다."));
   }
}
