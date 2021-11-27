package capstone.jakdu.User.repository;

import capstone.jakdu.User.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInfoRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);

    List<User> findAll();
}
