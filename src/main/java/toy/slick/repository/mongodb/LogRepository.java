package toy.slick.repository.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.sql.Timestamp;

public interface LogRepository extends MongoRepository<LogRepository.Log, String> {

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    class Log {
        private String log;
        private Timestamp timestamp;
    }
}