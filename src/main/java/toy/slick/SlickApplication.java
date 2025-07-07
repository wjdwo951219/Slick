package toy.slick;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import toy.slick.common.Const;

import java.time.ZoneId;
import java.util.TimeZone;

@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class SlickApplication {
    public static void main(String[] args) {
        SpringApplication.run(SlickApplication.class, args);
    }

    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(Const.ZoneId.UTC)));
    }
}
