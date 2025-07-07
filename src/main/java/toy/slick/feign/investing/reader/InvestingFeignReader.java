package toy.slick.feign.investing.reader;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import toy.slick.feign.inheritable.FeignResponseReader;
import toy.slick.feign.investing.vo.response.EconomicIndex;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
public class InvestingFeignReader implements FeignResponseReader {
    public Optional<EconomicIndex> getEconomicIndex(Response feignResponse) throws IOException {
        String responseBody = this.getResponseBody(feignResponse);

        Document document = Jsoup.parse(responseBody);

        String url = feignResponse.request().url();
        String price = this.getPrice(document);
        String priceChange = this.getPriceChange(document);
        String priceChangePercent = this.getPriceChangePercent(document);
        LocalDateTime utcDateTime = this.getUtcDateTime(document);
        String title = this.getTitle(document);

        return StringUtils.isBlank(price)
                ? Optional.empty()
                : Optional.of(EconomicIndex.builder()
                .price(price)
                .priceChange(priceChange)
                .priceChangePercent(priceChangePercent.substring(1, priceChangePercent.length() - 1))
                .utcDateTime(utcDateTime)
                .url(url)
                .title(title)
                .build());
    }

    private String getPrice(Document document) {
        return document.getElementsByAttributeValue("data-test", "instrument-price-last").first().text();
    }

    private String getPriceChange(Document document) {
        return document.getElementsByAttributeValue("data-test", "instrument-price-change").first().text();
    }

    private String getPriceChangePercent(Document document) {
        return document.getElementsByAttributeValue("data-test", "instrument-price-change-percent").first().text();
    }

    private String getTitle(Document document) {
        return document.getElementsByTag("h1").first().ownText();
    }

    private LocalDateTime getUtcDateTime(Document document) {
        return ZonedDateTime.parse(document.getElementsByAttributeValue("data-test", "trading-time-label").first().attr("datetime")).toLocalDateTime();
    }
}
