//package toy.slick.scheduler;
//
//import feign.Response;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.jooq.generated.tables.pojos.EconomicEvent;
//import org.jooq.generated.tables.pojos.FearAndGreed;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import toy.slick.aspect.TimeLogAspect;
//import toy.slick.common.Const;
//import toy.slick.exception.BlankException;
//import toy.slick.exception.EmptyException;
//import toy.slick.feign.telegram.TelegramFeign;
//import toy.slick.repository.mariadb.EconomicEventRepository;
//import toy.slick.repository.mariadb.FearAndGreedRepository;
//
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
//@EnableAsync
//@EnableScheduling
//public class TelegramScheduler {
//    private final String BOT_SLICK_TOKEN;
//    private final String CHAT_SLICK_ID;
//
//    /* Feign */
//    private final TelegramFeign telegramFeign;
//
//    /* Repository */
//    private final FearAndGreedRepository fearAndGreedRepository;
//    private final EconomicEventRepository economicEventRepository;
//    private final SPXRepository economicIndexRepository;
//
//    public TelegramScheduler(@Value("${telegram.bot.slick.token}") String BOT_SLICK_TOKEN,
//                             @Value("${telegram.chat.slick.id}") String CHAT_SLICK_ID,
//                             TelegramFeign telegramFeign,
//                             FearAndGreedRepository fearAndGreedRepository,
//                             EconomicEventRepository economicEventRepository,
//                             SPXRepository economicIndexRepository) {
//        this.BOT_SLICK_TOKEN = BOT_SLICK_TOKEN;
//        this.CHAT_SLICK_ID = CHAT_SLICK_ID;
//        this.telegramFeign = telegramFeign;
//        this.fearAndGreedRepository = fearAndGreedRepository;
//        this.economicEventRepository = economicEventRepository;
//        this.economicIndexRepository = economicIndexRepository;
//    }
//
//    @TimeLogAspect.TimeLog
//    @Async
//    @Scheduled(cron = "0 0 8 * * 1-5", zone = Const.ZoneId.SEOUL)
//    public void sendFearAndGreedForSouthKorea() {
//        try (Response response = telegramFeign.sendHtmlWithoutPreview(BOT_SLICK_TOKEN, CHAT_SLICK_ID,
//                this.getFearAndGreedTelegramMessage())) {
//            log.info(response.toString());
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    @TimeLogAspect.TimeLog
//    @Async
//    @Scheduled(cron = "0 30 8 * * 1-5", zone = Const.ZoneId.NEW_YORK)
//    public void sendFearAndGreedForUnitedStates() {
//        try (Response response = telegramFeign.sendHtmlWithoutPreview(BOT_SLICK_TOKEN, CHAT_SLICK_ID,
//                this.getFearAndGreedTelegramMessage())) {
//            log.info(response.toString());
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    @TimeLogAspect.TimeLog
//    @Async
//    @Scheduled(cron = "0 35 2 * * *", zone = Const.ZoneId.UTC)
//    public void sendYesterdayEconomicEventList() {
//        try {
//            ZonedDateTime yesterday = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
//                    .minusDays(1)
//                    .withHour(0)
//                    .withMinute(0)
//                    .withSecond(0);
//
//            try (Response response = telegramFeign.sendHtmlWithoutPreview(BOT_SLICK_TOKEN, CHAT_SLICK_ID,
//                    this.getEconomicEventListTelegramMessage(yesterday))) {
//                log.info(response.toString());
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    @TimeLogAspect.TimeLog
//    @Async
//    @Scheduled(cron = "0 25 8 * * 2-6", zone = Const.ZoneId.SEOUL)
//    public void sendIndices() {
//        try {
//            String telegramMessage = Arrays.stream(Const.EconomicIndex.values())
//                    .map(Const.EconomicIndex::getCode)
//                    .map(economicIndexRepository::select)
//                    .filter(Optional::isPresent)
//                    .map(Optional::get)
//                    .map(economicIndex -> {
//                        String title = economicIndex.getTitle();
//                        String url = economicIndex.getUrl();
//                        String price = economicIndex.getPrice();
//                        String priceChange = economicIndex.getPriceChange();
//                        String priceChangePercent = economicIndex.getPriceChangePercent();
//                        String titleIcon = priceChange.startsWith("-") ? Const.DOWN_CHART : Const.UP_CHART;
//
//                        return titleIcon + "<b><a href='" + url + "'>" + title + "</a></b>\n"
//                                + " - price : <b><u>" + price + "</u></b>\n"
//                                + " - change : <b><u>" + priceChange + " (" + priceChangePercent + ")</u></b>\n";
//                    })
//                    .collect(Collectors.joining());
//
//            if (StringUtils.isBlank(telegramMessage)) {
//                throw new BlankException("telegramMessage is Blank");
//            }
//
//            try (Response response = telegramFeign.sendHtmlWithoutPreview(BOT_SLICK_TOKEN, CHAT_SLICK_ID, telegramMessage)) {
//                log.info(response.toString());
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//
//    private String getFearAndGreedTelegramMessage() {
//        Optional<FearAndGreed> fearAndGreed = fearAndGreedRepository.selectRecentOne();
//
//        if (fearAndGreed.isEmpty()) {
//            throw new EmptyException("fearAndGreed is Empty");
//        }
//
//        String rating = fearAndGreed.get().getRating();
//        double score = fearAndGreed.get().getScore();
//        StringBuilder messageBuilder = new StringBuilder();
//
//        messageBuilder
//                .append("Current <a href='https://www.cnn.com/markets/fear-and-greed'>CNN Fear & Greed Index</a> Information").append("\n")
//                .append(" - Rating : ")
//                .append(rating)
//                .append("\n")
//                .append(" - Score : ")
//                .append(String.format("%.2f", score))
//                .append(" ");
//
//        if (score < 25) {
//            messageBuilder.append(Const.FACE_SCREAMING_IN_FEAR);
//        } else if (score < 30) {
//            messageBuilder.append(Const.FEARFUL_FACE);
//        } else if (score < 70) {
//            messageBuilder.append(Const.THINKING_FACE);
//        } else if (score < 75) {
//            messageBuilder.append(Const.GRINNING_SQUINTING_FACE);
//        } else {
//            messageBuilder.append(Const.ZANY_FACE);
//        }
//
//        return messageBuilder.toString();
//    }
//
//    private String getEconomicEventListTelegramMessage(ZonedDateTime targetDateTime) {
//        List<EconomicEvent> economicEventList = economicEventRepository
//                .select(targetDateTime.toLocalDateTime(), targetDateTime.plusDays(1).toLocalDateTime());
//
//        Map<String, List<String>> countryEconomicEventListMap = new HashMap<>();
//
//        for (EconomicEvent economicEvent : economicEventList) {
//            String eventId = economicEvent.getId();
//            String eventName = economicEvent.getName();
//            String country = economicEvent.getCountry();
//            String actualValue = economicEvent.getActual();
//            String forecastValue = StringUtils.defaultIfBlank(economicEvent.getForecast(), "-");
//            String previousValue = economicEvent.getPrevious();
//
//            String message = "<a href='https://m.investing.com/economic-calendar/" + eventId + "'>" + eventName + "</a>" + "\n"
//                    + " : " + actualValue + " | " + forecastValue + " | " + previousValue;
//
//            if (!countryEconomicEventListMap.containsKey(country)) {
//                countryEconomicEventListMap.put(country, new ArrayList<>());
//            }
//
//            countryEconomicEventListMap.get(country).add(message);
//        }
//
//        if (countryEconomicEventListMap.isEmpty()) {
//            return Const.CHECK_MARK
//                    + " <b>"
//                    + targetDateTime.format(Const.DateTimeFormat.yyyyMMdd_DotBlank.getDateTimeFormatter())
//                    + " [" + targetDateTime.getZone().getId() + "]\n"
//                    + "Important <a href='https://m.investing.com/economic-calendar/'>Economic Index List</a> is Empty</b>";
//        }
//
//        StringBuilder messageBuilder = new StringBuilder();
//
//        messageBuilder
//                .append(Const.CHECK_MARK)
//                .append(" <b>")
//                .append(targetDateTime.format(Const.DateTimeFormat.yyyyMMdd_DotBlank.getDateTimeFormatter()))
//                .append(" [")
//                .append(targetDateTime.getZone().getId())
//                .append("]")
//                .append("\n")
//                .append("Important <a href='https://m.investing.com/economic-calendar/'>Economic Index List</a></b>")
//                .append("\n")
//                .append(" : Actual | Forecast | Previous")
//                .append("\n")
//                .append("————————").append("\n");
//
//        for (String country : countryEconomicEventListMap.keySet()) {
//            messageBuilder
//                    .append(Const.FLAG)
//                    .append("<b>")
//                    .append(country)
//                    .append("</b>")
//                    .append("\n")
//                    .append(String.join("\n", countryEconomicEventListMap.get(country)))
//                    .append("\n")
//                    .append("————————")
//                    .append("\n");
//        }
//
//        return messageBuilder.toString();
//    }
//}
