package toy.slick;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import toy.slick.common.Const;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.TimeZone;

@Slf4j
@EnableFeignClients
@EnableScheduling
@SpringBootApplication
public class SlickApplication {
    private final DataSource dataSource;

    public SlickApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void setTimeZone() throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(Const.ZoneId.UTC)));

        try (Connection connection = dataSource.getConnection()) {
            log.info("Database[{}] connection established", connection.getMetaData().getDatabaseProductName());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SlickApplication.class, args);
    }
}
