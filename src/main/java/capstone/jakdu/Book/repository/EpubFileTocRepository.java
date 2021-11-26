package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.EPubToc;
import capstone.jakdu.Book.domain.EpubFileToc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpubFileTocRepository extends JpaRepository<EpubFileToc, Long> {
    List<EpubFileToc> findAllByBookIdOrderByIdDesc(Long bookId);
}
