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
import toy.slick.feign.investing.vo.response.Holiday;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

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
        String title = this.getTitle(document);

        return StringUtils.isBlank(price)
                ? Optional.empty()
                : Optional.of(EconomicIndex.builder()
                .price(price)
                .priceChange(priceChange)
                .priceChangePercent(priceChangePercent.substring(1, priceChangePercent.length() - 1))
                .url(url)
                .title(title)
                .build());
    }

    public List<Holiday> getHolidayList(Response feignResponse) throws IOException {
        String responseBody = this.getResponseBody(feignResponse);

        Queue<Element> rowQueue = new LinkedList<>(Jsoup.parse(responseBody)
                .getElementById("holidayCalendarData")
                .getElementsByTag("tbody").first()
                .getElementsByTag("tr"));

        List<Holiday> holidayList = new ArrayList<>();

        while (!rowQueue.isEmpty()) {
            Element firstRow = rowQueue.poll();

            String date = firstRow.getElementsByClass("date bold center").first().text();

            Set<String> countrySet = new HashSet<>();

            countrySet.add(firstRow.getElementsByClass("bold cur").first().text());

            while (!rowQueue.isEmpty() && StringUtils.isBlank(rowQueue.peek().getElementsByClass("date bold center").first().text())) {
                Element nextRow = rowQueue.poll();

                if (nextRow != null) {
                    countrySet.add(nextRow.getElementsByClass("bold cur").first().text());
                }
            }

            for (String country : countrySet) {
                holidayList.add(Holiday.builder()
                        .date(LocalDate.parse(date, DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)))
                        .country(country)
                        .build());
            }
        }

        return holidayList;
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
}
