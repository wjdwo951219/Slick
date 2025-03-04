package toy.slick.feign.naver.reader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import toy.slick.feign.inheritable.FeignResponseReader;
import toy.slick.feign.naver.vo.response.Currency;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class NaverFeignReader implements FeignResponseReader {
    public Optional<Currency> getCurrency(Response feignResponse) throws IOException {
        String responseBody = this.getResponseBody(feignResponse);

        JsonObject currencyJsonObj = JsonParser.parseString(responseBody)
                .getAsJsonObject()
                .get("result")
                .getAsJsonObject();

        String price = currencyJsonObj.get("closePrice").getAsString();
        String priceChange = currencyJsonObj.get("fluctuations").getAsString();
        String priceChangePercent = currencyJsonObj.get("fluctuationsRatio").getAsString() + "%";
        String url = currencyJsonObj.get("endUrl").getAsString();

        return StringUtils.isBlank(price)
                ? Optional.empty()
                : Optional.of(Currency.builder()
                .price(price)
                .priceChange(priceChange.startsWith("-") ? priceChange : "+" + priceChange)
                .priceChangePercent(priceChangePercent.startsWith("-") ? priceChangePercent : "+" + priceChangePercent)
                .url(url)
                .build());
    }
}
