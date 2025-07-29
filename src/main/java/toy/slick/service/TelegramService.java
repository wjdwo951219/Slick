package toy.slick.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.generated.tables.pojos.*;
import org.springframework.stereotype.Service;
import toy.slick.common.Const;
import toy.slick.common.MsgUtils;
import toy.slick.repository.mysql.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TelegramService {
    private final FearAndGreedRepository fearAndGreedRepository;
    private final EconomicEventRepository economicEventRepository;
    private final DjiRepository djiRepository;
    private final IxicRepository ixicRepository;
    private final SpxRepository spxRepository;
    private final KospiRepository kospiRepository;
    private final KosdaqRepository kosdaqRepository;
    private final CurrencyEurKrwRepository currencyEurKrwRepository;
    private final CurrencyJpyKrwRepository currencyJpyKrwRepository;
    private final CurrencyUsdKrwRepository currencyUsdKrwRepository;

    public TelegramService(FearAndGreedRepository fearAndGreedRepository,
                           EconomicEventRepository economicEventRepository,
                           DjiRepository djiRepository,
                           IxicRepository ixicRepository,
                           SpxRepository spxRepository,
                           KospiRepository kospiRepository,
                           KosdaqRepository kosdaqRepository,
                           CurrencyEurKrwRepository currencyEurKrwRepository,
                           CurrencyJpyKrwRepository currencyJpyKrwRepository,
                           CurrencyUsdKrwRepository currencyUsdKrwRepository) {
        this.fearAndGreedRepository = fearAndGreedRepository;
        this.economicEventRepository = economicEventRepository;
        this.djiRepository = djiRepository;
        this.ixicRepository = ixicRepository;
        this.spxRepository = spxRepository;
        this.kospiRepository = kospiRepository;
        this.kosdaqRepository = kosdaqRepository;
        this.currencyEurKrwRepository = currencyEurKrwRepository;
        this.currencyJpyKrwRepository = currencyJpyKrwRepository;
        this.currencyUsdKrwRepository = currencyUsdKrwRepository;
    }

    public String getDjiMessage() {
        Optional<Dji> dji = djiRepository.selectRecentOneIn12Hours();

        return dji.map(value -> this.generateIndexMessage(
                        value.getPriceChange().startsWith("-") ? Const.Emoji.DOWN_CHART : Const.Emoji.UP_CHART,
                        value.getUrl(),
                        value.getTitle(),
                        value.getPrice(),
                        value.getPriceChange(),
                        value.getPriceChangePercent()))
                .orElse(StringUtils.EMPTY);
    }

    public String getSpxMessage() {
        Optional<Spx> spx = spxRepository.selectRecentOneIn12Hours();

        return spx.map(value -> this.generateIndexMessage(
                        value.getPriceChange().startsWith("-") ? Const.Emoji.DOWN_CHART : Const.Emoji.UP_CHART,
                        value.getUrl(),
                        value.getTitle(),
                        value.getPrice(),
                        value.getPriceChange(),
                        value.getPriceChangePercent()))
                .orElse(StringUtils.EMPTY);

    }

    public String getIxicMessage() {
        Optional<Ixic> ixic = ixicRepository.selectRecentOneIn12Hours();

        return ixic.map(value -> this.generateIndexMessage(
                        value.getPriceChange().startsWith("-") ? Const.Emoji.DOWN_CHART : Const.Emoji.UP_CHART,
                        value.getUrl(),
                        value.getTitle(),
                        value.getPrice(),
                        value.getPriceChange(),
                        value.getPriceChangePercent()))
                .orElse(StringUtils.EMPTY);

    }

    public String getKosdaqMessage() {
        Optional<Kosdaq> kosdaq = kosdaqRepository.selectRecentOneIn12Hours();

        return kosdaq.map(value -> this.generateIndexMessage(
                        value.getPriceChange().startsWith("-") ? Const.Emoji.DOWN_CHART : Const.Emoji.UP_CHART,
                        value.getUrl(),
                        value.getTitle(),
                        value.getPrice(),
                        value.getPriceChange(),
                        value.getPriceChangePercent()))
                .orElse(StringUtils.EMPTY);

    }

    public String getKospiMessage() {
        Optional<Kospi> kospi = kospiRepository.selectRecentOneIn12Hours();

        return kospi.map(value -> this.generateIndexMessage(
                        value.getPriceChange().startsWith("-") ? Const.Emoji.DOWN_CHART : Const.Emoji.UP_CHART,
                        value.getUrl(),
                        value.getTitle(),
                        value.getPrice(),
                        value.getPriceChange(),
                        value.getPriceChangePercent()))
                .orElse(StringUtils.EMPTY);

    }

    private String generateIndexMessage(String titleIcon, String url, String title, String price, String priceChange, String priceChangePercent) {
        return titleIcon + "<b><a href='" + url + "'>" + title + "</a></b>\n"
                + " - price : <b><u>" + price + "</u></b>\n"
                + " - change : <b><u>" + priceChange + " (" + priceChangePercent + ")</u></b>\n";
    }

    public String getFearAndGreedMessage() throws Exception {
        Optional<FearAndGreed> fearAndGreed = fearAndGreedRepository.selectRecentOneIn12Hours();

        if (fearAndGreed.isEmpty()) {
            throw new Exception(MsgUtils.emptyMsg(fearAndGreed));
        }

        String rating = fearAndGreed.get().getRating();
        double score = fearAndGreed.get().getScore();
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder
                .append("Current <a href='https://www.cnn.com/markets/fear-and-greed'><b>CNN Fear & Greed Index</b></a> Information").append("\n")
                .append(" - Rating : <b><u>")
                .append(rating)
                .append("</u></b>\n")
                .append(" - Score : <b><u>")
                .append(String.format("%.2f", score))
                .append("</u></b> ");

        if (score < 25) {
            messageBuilder.append(Const.Emoji.FACE_SCREAMING_IN_FEAR);
        } else if (score < 30) {
            messageBuilder.append(Const.Emoji.FEARFUL_FACE);
        } else if (score < 50) {
            messageBuilder.append(Const.Emoji.THINKING_FACE);
        } else if (score < 70) {
            messageBuilder.append(Const.Emoji.SLIGHTLY_SMILING_FACE);
        } else if (score < 75) {
            messageBuilder.append(Const.Emoji.GRINNING_SQUINTING_FACE);
        } else {
            messageBuilder.append(Const.Emoji.ZANY_FACE);
        }

        return messageBuilder.toString();
    }

    public String getEconomicEventListMessage(Const.Country country, String minImportance, ZonedDateTime startDateTime, ZonedDateTime endDateTime) {
        List<EconomicEvent> economicEventList = economicEventRepository
                .select(country.getCountryName(),
                        startDateTime.withZoneSameInstant(ZoneId.of(Const.ZoneId.UTC)).toLocalDateTime(),
                        endDateTime.withZoneSameInstant(ZoneId.of(Const.ZoneId.UTC)).toLocalDateTime())
                .stream()
                .filter(economicEvent -> economicEvent.getImportance().compareTo(minImportance) >= 0)
                .toList();

        if (CollectionUtils.isEmpty(economicEventList)) {
            return Const.Emoji.CHECK_MARK
                    + "<b><a href='https://tradingeconomics.com/calendar'>Economic Event List</a> is Empty</b>";
        }

        Map<String, List<String>> economicEventListMap = economicEventList
                .stream()
                .collect(Collectors.groupingBy(EconomicEvent::getCountry,
                        Collectors.mapping(economicEvent -> {
                            String url = economicEvent.getUrl();
                            String eventName = economicEvent.getName();
                            String actualValue = economicEvent.getActual();
                            String forecastValue = StringUtils.defaultIfBlank(economicEvent.getForecast(), "-");
                            String previousValue = economicEvent.getPrevious();

                            return "<a href='" + url + "'>" + eventName + "</a>" + "\n"
                                    + " : " + actualValue + " | " + forecastValue + " | " + previousValue;
                        }, Collectors.toList())));

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder
                .append(Const.Emoji.CHECK_MARK)
                .append("<b><a href='https://tradingeconomics.com/calendar'>Economic Event List</a></b>")
                .append("\n")
                .append(" : Actual | Forecast | Previous")
                .append("\n")
                .append("————————").append("\n");

        for (Map.Entry<String, List<String>> economicEventListEntry : economicEventListMap.entrySet()) {
            messageBuilder
                    .append(Const.Emoji.FLAG)
                    .append("<b>")
                    .append(economicEventListEntry.getKey())
                    .append("</b>")
                    .append("\n")
                    .append(economicEventListEntry.getValue()
                            .stream()
                            .sorted()
                            .collect(Collectors.joining("\n")))
                    .append("\n")
                    .append("————————")
                    .append("\n");
        }

        return messageBuilder.toString();
    }

    public String getCurrencyEurKrwMessage() {
        Optional<CurrencyEurKrw> currencyEurKrw = currencyEurKrwRepository.selectRecentOneIn12Hours();

        return currencyEurKrw.map(eurKrw -> this.generateCurrencyMessage(
                        Const.Emoji.EURO_BANKNOTE,
                        eurKrw.getUrl(),
                        "1€(EUR)",
                        eurKrw.getPrice(),
                        eurKrw.getPriceChange(),
                        eurKrw.getPriceChangePercent()))
                .orElse(StringUtils.EMPTY);

    }

    public String getCurrencyJpyKrwMessage() {
        Optional<CurrencyJpyKrw> currencyJpyKrw = currencyJpyKrwRepository.selectRecentOneIn12Hours();

        return currencyJpyKrw.map(jpyKrw -> this.generateCurrencyMessage(
                        Const.Emoji.YEN_BANKNOTE,
                        jpyKrw.getUrl(),
                        "1¥(JPY)",
                        jpyKrw.getPrice(),
                        jpyKrw.getPriceChange(),
                        jpyKrw.getPriceChangePercent()))
                .orElse(StringUtils.EMPTY);

    }

    public String getCurrencyUsdKrwMessage() {
        Optional<CurrencyUsdKrw> currencyJpyKrw = currencyUsdKrwRepository.selectRecentOneIn12Hours();

        return currencyJpyKrw.map(currencyUsdKrw -> this.generateCurrencyMessage(
                        Const.Emoji.DOLLAR_BANKNOTE,
                        currencyUsdKrw.getUrl(),
                        "1$(USD)",
                        currencyUsdKrw.getPrice(),
                        currencyUsdKrw.getPriceChange(),
                        currencyUsdKrw.getPriceChangePercent()))
                .orElse(StringUtils.EMPTY);

    }

    private String generateCurrencyMessage(String titleIcon, String url, String title, String price, String priceChange, String priceChangePercent) {
        return titleIcon + "<b><a href='" + url + "'>" + title + "</a></b>\n"
                + " = <u>" + price + "₩</u> (" + priceChange + ", " + priceChangePercent + ")\n";
    }
}
