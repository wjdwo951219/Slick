package toy.slick.repository.mongodb;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Getter
public abstract class MongoData<T> {
    @Id
    protected String _id = StringUtils.EMPTY;
    protected Date _timestamp;

    public abstract T toMongoData(String _id);
}
