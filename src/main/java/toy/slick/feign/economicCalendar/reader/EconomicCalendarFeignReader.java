package toy.slick.feign.economicCalendar.reader;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import toy.slick.common.Const;
import toy.slick.feign.economicCalendar.vo.response.EconomicEvent;
import toy.slick.feign.inheritable.FeignResponseReader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EconomicCalendarFeignReader implements FeignResponseReader {

    public List<EconomicEvent> getEconomicEventList(Response feignResponse) throws IOException {
        String responseBody = this.getResponseBody(feignResponse);

        Element table = Jsoup.parse(responseBody).getElementById("calendar");
        Elements rows = table.select("tbody tr");

        return rows.stream()
                .filter(row -> !row.getElementsByClass("calendar-event").isEmpty())
                .filter(row -> !StringUtils.isBlank(row.firstElementChild().getElementsByTag("span").first().text()))
                .map(row -> {
                    String date = row.firstElementChild().attr("class").trim();
                    String time = row.firstElementChild().getElementsByTag("span").first().text().trim().substring(0, 5);

                    ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.parse(date + time, DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm")), ZoneId.of(Const.ZoneId.UTC));
                    String country = row.getElementsByTag("table").first().getElementsByTag("div").first().attr("title");
                    String importanceStr = row.firstElementChild().getElementsByTag("span").first().attr("class");
                    String importance = importanceStr.substring(importanceStr.length() - 1);
                    String url = "https://tradingeconomics.com" + row.getElementsByClass("calendar-event").first().attr("href");
                    String name = row.getElementsByClass("calendar-event").first().text();

                    Element actualElement = row.getElementById("actual");
                    Element forecastElement = row.getElementById("forecast");
                    Element previousElement = row.getElementById("previous");

                    String actual = actualElement != null ? actualElement.text() : StringUtils.EMPTY;
                    String forecast = forecastElement != null ? forecastElement.text() : StringUtils.EMPTY;
                    String previous = previousElement != null ? previousElement.text() : StringUtils.EMPTY;

                    return EconomicEvent.builder()
                            .url(url)
                            .name(name)
                            .zonedDateTime(zonedDateTime)
                            .country(country)
                            .importance(importance)
                            .actual(actual)
                            .forecast(forecast)
                            .previous(previous)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
