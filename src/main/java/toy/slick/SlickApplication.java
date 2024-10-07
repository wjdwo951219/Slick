package toy.slick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class SlickApplication {
	// TODO : 기존 Slick project 기능 적용
	public static void main(String[] args) {
		SpringApplication.run(SlickApplication.class, args);
	}

}
