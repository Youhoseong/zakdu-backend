package capstone.jakdu.Book.repository;

import capstone.jakdu.Book.domain.PurchasedPageList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchasedPageListRepository extends JpaRepository<PurchasedPageList, Long> {
}
