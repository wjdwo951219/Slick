package toy.slick.feign.cnn.reader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import toy.slick.feign.cnn.vo.response.FearAndGreed;
import toy.slick.feign.inheritable.FeignResponseReader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Component
public class CnnFeignReader implements FeignResponseReader {

    public Optional<FearAndGreed> getFearAndGreed(Response feignResponse) throws IOException {
        String responseBody = this.getResponseBody(feignResponse);

        JsonObject fearAndGreedJsonObj = JsonParser.parseString(responseBody)
                .getAsJsonObject()
                .get("fear_and_greed")
                .getAsJsonObject();

        String rating = fearAndGreedJsonObj.get("rating").getAsString();
        double score = Double.parseDouble(fearAndGreedJsonObj.get("score").getAsString());
        LocalDateTime utcDateTime = ZonedDateTime.parse(fearAndGreedJsonObj.get("timestamp").getAsString()).toLocalDateTime();

        return Optional.of(FearAndGreed.builder()
                .rating(rating)
                .score(score)
                .utcDateTime(utcDateTime)
                .build());
    }
}
