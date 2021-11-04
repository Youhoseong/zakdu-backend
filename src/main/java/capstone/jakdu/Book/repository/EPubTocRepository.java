package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.EPubToc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EPubTocRepository extends JpaRepository<EPubToc, Long> {
}
