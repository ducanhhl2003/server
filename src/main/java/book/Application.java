package book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
//@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
