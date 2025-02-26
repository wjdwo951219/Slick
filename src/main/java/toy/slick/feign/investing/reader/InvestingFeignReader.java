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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    public Map<String, Set<String>> getHolidayDateAndCountryListMap(Response feignResponse) throws IOException {
        String responseBody = this.getResponseBody(feignResponse);

        Document document = Jsoup.parse(responseBody);

        Element holidayTable = document.getElementById("holidayCalendarData");

        Element holidayTableBody = holidayTable.getElementsByTag("tbody").first();

        Queue<Element> rowQueue = new LinkedList<>(holidayTableBody.getElementsByTag("tr"));
        Map<String, Set<String>> holidayDateAndCountrySetMap = new HashMap<>();


        while (!rowQueue.isEmpty()) {
            Element row = rowQueue.poll();

            String date = row.getElementsByClass("date bold center").first().text();
            Set<String> countrySet = new HashSet<>();
            countrySet.add(row.getElementsByClass("bold cur").first().text());

            while (!rowQueue.isEmpty() && StringUtils.isBlank(rowQueue.peek().getElementsByClass("date bold center").first().text())) {
                countrySet.add(rowQueue.poll().getElementsByClass("bold cur").first().text());
            }

            holidayDateAndCountrySetMap.put(date, countrySet);
        }

        return holidayDateAndCountrySetMap;
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
