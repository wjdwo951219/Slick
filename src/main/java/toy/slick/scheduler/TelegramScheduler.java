package toy.slick.scheduler;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.generated.tables.pojos.Dji;
import org.jooq.generated.tables.pojos.EconomicEvent;
import org.jooq.generated.tables.pojos.FearAndGreed;
import org.jooq.generated.tables.pojos.Ixic;
import org.jooq.generated.tables.pojos.Kosdaq;
import org.jooq.generated.tables.pojos.Kospi;
import org.jooq.generated.tables.pojos.Spx;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Const;
import toy.slick.feign.telegram.TelegramFeign;
import toy.slick.repository.mariadb.DjiRepository;
import toy.slick.repository.mariadb.EconomicEventRepository;
import toy.slick.repository.mariadb.FearAndGreedRepository;
import toy.slick.repository.mariadb.HolidayRepository;
import toy.slick.repository.mariadb.IxicRepository;
import toy.slick.repository.mariadb.KosdaqRepository;
import toy.slick.repository.mariadb.KospiRepository;
import toy.slick.repository.mariadb.SpxRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class TelegramScheduler {
    private final String BOT_SLICK_TOKEN;
    private final String CHAT_SLICK_US_ID;
    private final String CHAT_SLICK_KR_ID;

    /* Feign */
    private final TelegramFeign telegramFeign;

    /* Repository */
    private final HolidayRepository holidayRepository;
    private final FearAndGreedRepository fearAndGreedRepository;
    private final EconomicEventRepository economicEventRepository;
    private final DjiRepository djiRepository;
    private final IxicRepository ixicRepository;
    private final SpxRepository spxRepository;
    private final KospiRepository kospiRepository;
    private final KosdaqRepository kosdaqRepository;

// TODO : 국가별 분리, refactoring
    public TelegramScheduler(@Value("${telegram.bot.slick.token}") String BOT_SLICK_TOKEN,
                             @Value("${telegram.chat.slick.us.id}") String CHAT_SLICK_US_ID,
                             @Value("${telegram.chat.slick.kr.id}") String CHAT_SLICK_KR_ID,
                             TelegramFeign telegramFeign,
                             FearAndGreedRepository fearAndGreedRepository,
                             EconomicEventRepository economicEventRepository,
                             DjiRepository djiRepository,
                             IxicRepository ixicRepository,
                             SpxRepository spxRepository,
                             HolidayRepository holidayRepository,
                             KospiRepository kospiRepository,
                             KosdaqRepository kosdaqRepository) {
        this.BOT_SLICK_TOKEN = BOT_SLICK_TOKEN;
        this.CHAT_SLICK_US_ID = CHAT_SLICK_US_ID;
        this.CHAT_SLICK_KR_ID = CHAT_SLICK_KR_ID;
        this.telegramFeign = telegramFeign;
        this.fearAndGreedRepository = fearAndGreedRepository;
        this.economicEventRepository = economicEventRepository;
        this.djiRepository = djiRepository;
        this.ixicRepository = ixicRepository;
        this.spxRepository = spxRepository;
        this.holidayRepository = holidayRepository;
        this.kospiRepository = kospiRepository;
        this.kosdaqRepository = kosdaqRepository;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 15 18 * * 1-5", zone = Const.ZoneId.NEW_YORK)
    public void sendFearAndGreedForUnitedStates() {
        if (holidayRepository.select("United States", LocalDate.now(ZoneId.of(Const.ZoneId.NEW_YORK))).isPresent()) {
            return;
        }

        try (Response response = telegramFeign.sendHtmlWithoutPreview(BOT_SLICK_TOKEN, CHAT_SLICK_US_ID,
                this.getFearAndGreedTelegramMessage())) {
            log.info(response.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 0 8 * * *", zone = Const.ZoneId.NEW_YORK)
    public void sendEconomicEventListForUnitedStates() {
        try {
            ZonedDateTime searchDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.NEW_YORK))
                    .minusDays(1)
                    .withHour(7)
                    .withMinute(49)
                    .withSecond(0);

            try (Response response = telegramFeign.sendHtmlWithoutPreview(BOT_SLICK_TOKEN, CHAT_SLICK_US_ID,
                    this.getEconomicEventListTelegramMessage("United States", searchDateTime))) {
                log.info(response.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 0 8 * * *", zone = Const.ZoneId.SEOUL)
    public void sendEconomicEventListForSouthKorea() {
        try {
            ZonedDateTime searchDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.SEOUL))
                    .minusDays(1)
                    .withHour(7)
                    .withMinute(49)
                    .withSecond(0);

            try (Response response = telegramFeign.sendHtmlWithoutPreview(BOT_SLICK_TOKEN, CHAT_SLICK_KR_ID,
                    this.getEconomicEventListTelegramMessage("South Korea", searchDateTime))) {
                log.info(response.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 20 18 * * 1-5", zone = Const.ZoneId.NEW_YORK)
    public void sendIndicesForUnitedStates() {
        if (holidayRepository.select("United States", LocalDate.now(ZoneId.of(Const.ZoneId.NEW_YORK))).isPresent()) {
            return;
        }

        try {
            Optional<Dji> dji = djiRepository.selectRecentOne();
            Optional<Ixic> ixic = ixicRepository.selectRecentOne();
            Optional<Spx> spx = spxRepository.selectRecentOne();

            StringBuilder telegramMessageBuilder = new StringBuilder();

            if (dji.isPresent()) {
                String titleIcon = dji.get().getPriceChange().startsWith("-") ? Const.DOWN_CHART : Const.UP_CHART;

                telegramMessageBuilder.append(titleIcon + "<b><a href='" + dji.get().getUrl() + "'>" + dji.get().getTitle() + "</a></b>\n"
                        + " - price : <b><u>" + dji.get().getPrice() + "</u></b>\n"
                        + " - change : <b><u>" + dji.get().getPriceChange() + " (" + dji.get().getPriceChangePercent() + ")</u></b>\n");
            }

            if (spx.isPresent()) {
                String titleIcon = spx.get().getPriceChange().startsWith("-") ? Const.DOWN_CHART : Const.UP_CHART;

                telegramMessageBuilder.append(titleIcon + "<b><a href='" + spx.get().getUrl() + "'>" + spx.get().getTitle() + "</a></b>\n"
                        + " - price : <b><u>" + spx.get().getPrice() + "</u></b>\n"
                        + " - change : <b><u>" + spx.get().getPriceChange() + " (" + spx.get().getPriceChangePercent() + ")</u></b>\n");
            }

            if (ixic.isPresent()) {
                String titleIcon = ixic.get().getPriceChange().startsWith("-") ? Const.DOWN_CHART : Const.UP_CHART;

                telegramMessageBuilder.append(titleIcon + "<b><a href='" + ixic.get().getUrl() + "'>" + ixic.get().getTitle() + "</a></b>\n"
                        + " - price : <b><u>" + ixic.get().getPrice() + "</u></b>\n"
                        + " - change : <b><u>" + ixic.get().getPriceChange() + " (" + ixic.get().getPriceChangePercent() + ")</u></b>\n");
            }

            if (telegramMessageBuilder.isEmpty()) {
                throw new Exception("telegramMessageBuilder is Empty");
            }

            try (Response response = telegramFeign.sendHtmlWithoutPreview(BOT_SLICK_TOKEN, CHAT_SLICK_US_ID, telegramMessageBuilder.toString())) {
                log.info(response.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "0 20 18 * * 1-5", zone = Const.ZoneId.SEOUL)
    public void sendIndicesForSouthKorea() {
        if (holidayRepository.select("South Korea", LocalDate.now(ZoneId.of(Const.ZoneId.SEOUL))).isPresent()) {
            return;
        }

        try {
            Optional<Kospi> kospi = kospiRepository.selectRecentOne();
            Optional<Kosdaq> kosdaq = kosdaqRepository.selectRecentOne();

            StringBuilder telegramMessageBuilder = new StringBuilder();

            if (kospi.isPresent()) {
                String titleIcon = kospi.get().getPriceChange().startsWith("-") ? Const.DOWN_CHART : Const.UP_CHART;

                telegramMessageBuilder.append(titleIcon + "<b><a href='" + kospi.get().getUrl() + "'>" + kospi.get().getTitle() + "</a></b>\n"
                        + " - price : <b><u>" + kospi.get().getPrice() + "</u></b>\n"
                        + " - change : <b><u>" + kospi.get().getPriceChange() + " (" + kospi.get().getPriceChangePercent() + ")</u></b>\n");
            }

            if (kosdaq.isPresent()) {
                String titleIcon = kosdaq.get().getPriceChange().startsWith("-") ? Const.DOWN_CHART : Const.UP_CHART;

                telegramMessageBuilder.append(titleIcon + "<b><a href='" + kosdaq.get().getUrl() + "'>" + kosdaq.get().getTitle() + "</a></b>\n"
                        + " - price : <b><u>" + kosdaq.get().getPrice() + "</u></b>\n"
                        + " - change : <b><u>" + kosdaq.get().getPriceChange() + " (" + kosdaq.get().getPriceChangePercent() + ")</u></b>\n");
            }

            if (telegramMessageBuilder.isEmpty()) {
                throw new Exception("telegramMessageBuilder is Empty");
            }

            try (Response response = telegramFeign.sendHtmlWithoutPreview(BOT_SLICK_TOKEN, CHAT_SLICK_KR_ID, telegramMessageBuilder.toString())) {
                log.info(response.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getFearAndGreedTelegramMessage() throws Exception {
        Optional<FearAndGreed> fearAndGreed = fearAndGreedRepository.selectRecentOne();

        if (fearAndGreed.isEmpty()) {
            throw new Exception("fearAndGreed is Empty");
        }

        String rating = fearAndGreed.get().getRating();
        double score = fearAndGreed.get().getScore();
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder
                .append("Current <a href='https://www.cnn.com/markets/fear-and-greed'>CNN Fear & Greed Index</a> Information").append("\n")
                .append(" - Rating : <b><u>")
                .append(rating)
                .append("</b></u>\n")
                .append(" - Score : <b><u>")
                .append(String.format("%.2f", score))
                .append("</b></u> ");

        if (score < 25) {
            messageBuilder.append(Const.FACE_SCREAMING_IN_FEAR);
        } else if (score < 30) {
            messageBuilder.append(Const.FEARFUL_FACE);
        } else if (score < 70) {
            messageBuilder.append(Const.THINKING_FACE);
        } else if (score < 75) {
            messageBuilder.append(Const.GRINNING_SQUINTING_FACE);
        } else {
            messageBuilder.append(Const.ZANY_FACE);
        }

        return messageBuilder.toString();
    }

    private String getEconomicEventListTelegramMessage(String targetCountry, ZonedDateTime targetDateTime) {
        List<EconomicEvent> economicEventList = economicEventRepository
                .select(targetCountry,
                        targetDateTime.withZoneSameInstant(ZoneId.of(Const.ZoneId.UTC)).toLocalDateTime(),
                        targetDateTime.plusDays(1).withZoneSameInstant(ZoneId.of(Const.ZoneId.UTC)).toLocalDateTime());

        Map<String, List<String>> countryEconomicEventListMap = new HashMap<>();

        for (EconomicEvent economicEvent : economicEventList) {
            String eventId = economicEvent.getId();
            String eventName = economicEvent.getName();
            String country = economicEvent.getCountry();
            String actualValue = economicEvent.getActual();
            String forecastValue = StringUtils.defaultIfBlank(economicEvent.getForecast(), "-");
            String previousValue = economicEvent.getPrevious();

            String message = "<a href='https://m.investing.com/economic-calendar/" + eventId + "'>" + eventName + "</a>" + "\n"
                    + " : " + actualValue + " | " + forecastValue + " | " + previousValue;

            if (!countryEconomicEventListMap.containsKey(country)) {
                countryEconomicEventListMap.put(country, new ArrayList<>());
            }

            countryEconomicEventListMap.get(country).add(message);
        }

        if (countryEconomicEventListMap.isEmpty()) {
            return Const.CHECK_MARK
                    + "<b><a href='https://m.investing.com/economic-calendar/'>Economic Event List</a> is Empty</b>";
        }

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder
                .append(Const.CHECK_MARK)
                .append("<b><a href='https://m.investing.com/economic-calendar/'>Economic Event List</a></b>")
                .append("\n")
                .append(" : Actual | Forecast | Previous")
                .append("\n")
                .append("————————").append("\n");

        for (String country : countryEconomicEventListMap.keySet()) {
            messageBuilder
                    .append(Const.FLAG)
                    .append("<b>")
                    .append(country)
                    .append("</b>")
                    .append("\n")
                    .append(String.join("\n", countryEconomicEventListMap.get(country)))
                    .append("\n")
                    .append("————————")
                    .append("\n");
        }

        return messageBuilder.toString();
    }
}
