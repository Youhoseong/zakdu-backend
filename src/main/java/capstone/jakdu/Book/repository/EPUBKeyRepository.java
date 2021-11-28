package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.EPUBKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EPUBKeyRepository extends JpaRepository<EPUBKey, Long> {
}
