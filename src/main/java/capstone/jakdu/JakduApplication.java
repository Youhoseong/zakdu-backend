package capstone.jakdu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class JakduApplication {

	public static void main(String[] args) {
		SpringApplication.run(JakduApplication.class, args);
	}

}
