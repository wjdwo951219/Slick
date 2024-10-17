package toy.slick.repository.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.sql.Timestamp;
import java.util.List;

public interface LogRepository extends MongoRepository<LogRepository.Log, String> {

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    class Log extends MongoData<Log> {
        private String log;

        @Override
        public Log toMongoData(String _id) {
            this._id = _id;
            this._timestamp = new Timestamp(System.currentTimeMillis());

            return this;
        }
    }

    long deleteAllBy_idIn(List<String> _idList);
}
