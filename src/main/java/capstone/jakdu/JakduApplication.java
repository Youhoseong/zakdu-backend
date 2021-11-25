package capstone.jakdu;

import capstone.jakdu.User.domain.User;
import capstone.jakdu.User.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class JakduApplication {
	@Autowired
	private UserInfoRepository repository;
	@PostConstruct
	public void initUsers() {
		User user1 = new User("javatechie", "email","password", "javatechie@gmail.com", 10000L);
		User user2 = new User("user1", "email","pwd1", "user1@gmail.com",10000L);
		User user3 = new User("user2", "email","pwd2", "user2@gmail.com",10000L);
		repository.save(user1);
		repository.save(user2);
		repository.save(user3);
	}

	public static void main(String[] args) {
		SpringApplication.run(JakduApplication.class, args);
	}

}
