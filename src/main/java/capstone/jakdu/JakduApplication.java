package capstone.jakdu;

import capstone.jakdu.User.domain.User;
import capstone.jakdu.User.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EnableTransactionManagement
public class JakduApplication {
	@Autowired
	private UserInfoRepository repository;
//	@PostConstruct
//	public void initUsers() {
//		User user1 = new User("javatechie", "email1@gmail.com","password", "customer", 10000L);
//		User user2 = new User("user1", "email2@gmail.com","pwd1", "seller",10000L);
//		User user3 = new User("user2", "email3@gmail.com","pwd2", "customer",10000L);
//		repository.save(user1);
//		repository.save(user2);
//		repository.save(user3);
//	}

	public static void main(String[] args) {
		SpringApplication.run(JakduApplication.class, args);
	}

}
