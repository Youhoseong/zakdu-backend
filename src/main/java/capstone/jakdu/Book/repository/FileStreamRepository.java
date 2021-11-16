package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.FileStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileStreamRepository  extends JpaRepository<FileStream, Long> {

}
