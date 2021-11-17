package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.PDFKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface PDFKeyRepository extends JpaRepository<PDFKey, Long> {
    Optional<PDFKey> findById(Long id);
}
