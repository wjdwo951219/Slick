package toy.slick.scheduler;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.generated.tables.pojos.Dji;
import org.jooq.generated.tables.pojos.Holiday;
import org.jooq.generated.tables.pojos.Ixic;
import org.jooq.generated.tables.pojos.Spx;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.feign.cnn.CnnFeign;
import toy.slick.feign.cnn.reader.CnnFeignReader;
import toy.slick.feign.cnn.vo.response.FearAndGreed;
import toy.slick.feign.economicCalendar.EconomicCalendarFeign;
import toy.slick.feign.economicCalendar.reader.EconomicCalendarFeignReader;
import toy.slick.feign.economicCalendar.vo.response.EconomicEvent;
import toy.slick.feign.investing.InvestingFeign;
import toy.slick.feign.investing.reader.InvestingFeignReader;
import toy.slick.feign.investing.vo.response.EconomicIndex;
import toy.slick.repository.mariadb.DjiRepository;
import toy.slick.repository.mariadb.EconomicEventRepository;
import toy.slick.repository.mariadb.FearAndGreedRepository;
import toy.slick.repository.mariadb.HolidayRepository;
import toy.slick.repository.mariadb.IxicRepository;
import toy.slick.repository.mariadb.SpxRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class DataSaveScheduler {
    /* Feign */
    private final CnnFeign cnnFeign;
    private final EconomicCalendarFeign economicCalendarFeign;
    private final InvestingFeign investingFeign;

    /* FeignReader */
    private final CnnFeignReader cnnFeignReader;
    private final EconomicCalendarFeignReader economicCalendarFeignReader;
    private final InvestingFeignReader investingFeignReader;

    /* Repository */
    private final EconomicEventRepository economicEventRepository;
    private final FearAndGreedRepository fearAndGreedRepository;
    private final DjiRepository djiRepository;
    private final IxicRepository ixicRepository;
    private final SpxRepository spxRepository;
    private final HolidayRepository holidayRepository;

    public DataSaveScheduler(CnnFeign cnnFeign,
                             EconomicCalendarFeign economicCalendarFeign,
                             InvestingFeign investingFeign,
                             CnnFeignReader cnnFeignReader,
                             EconomicCalendarFeignReader economicCalendarFeignReader,
                             InvestingFeignReader investingFeignReader,
                             EconomicEventRepository economicEventRepository,
                             FearAndGreedRepository fearAndGreedRepository,
                             DjiRepository djiRepository,
                             IxicRepository ixicRepository,
                             SpxRepository spxRepository,
                             HolidayRepository holidayRepository) {
        this.cnnFeign = cnnFeign;
        this.economicCalendarFeign = economicCalendarFeign;
        this.investingFeign = investingFeign;
        this.cnnFeignReader = cnnFeignReader;
        this.economicCalendarFeignReader = economicCalendarFeignReader;
        this.investingFeignReader = investingFeignReader;
        this.economicEventRepository = economicEventRepository;
        this.fearAndGreedRepository = fearAndGreedRepository;
        this.djiRepository = djiRepository;
        this.ixicRepository = ixicRepository;
        this.spxRepository = spxRepository;
        this.holidayRepository = holidayRepository;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "8 8,18,28,38,48,58 * * * *")
    public void saveEconomicEventList() throws IOException {
        try (Response response = economicCalendarFeign.getEconomicCalendar()) {
            Map<String, EconomicEvent> economicEventMap = economicCalendarFeignReader.getEconomicEventList(response)
                    .stream()
                    .filter(o -> StringUtils.isNotBlank(o.getActual()))
                    .sorted(Comparator.comparing(EconomicEvent::getZonedDateTime))
                    .collect(Collectors.toMap(EconomicEvent::getId, Function.identity(), (o1, o2) -> o2));

            if (CollectionUtils.isEmpty(economicEventMap.keySet())) {
                log.info("economicEventMap.keySet() is Empty");
                return;
            }

            economicEventRepository.delete(economicEventMap.keySet());

            int insertCnt = economicEventRepository.insertBatch(
                    economicEventMap.values()
                            .stream()
                            .map(o -> org.jooq.generated.tables.pojos.EconomicEvent.builder()
                                    .id(o.getId())
                                    .name(o.getName())
                                    .datetime(o.getZonedDateTime().toLocalDateTime())
                                    .country(o.getCountry())
                                    .importance(o.getImportance())
                                    .actual(o.getActual())
                                    .forecast(o.getForecast())
                                    .previous(o.getPrevious())
                                    .build())
                            .collect(Collectors.toList()),
                    10,
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != economicEventMap.values().size()) {
                log.error("insertCnt != economicEventMap.values().size()");
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "10 9,19,29,39,49,59 * * * *")
    public void saveFearAndGreed() throws IOException {
        try (Response response = cnnFeign.getFearAndGreed()) {
            Optional<FearAndGreed> fearAndGreed = cnnFeignReader.getFearAndGreed(response);

            if (fearAndGreed.isEmpty()) {
                log.error("fearAndGreed is Empty"); // TODO: Exception message -> property
                return;
            }

            int insertCnt = fearAndGreedRepository.insert(
                    org.jooq.generated.tables.pojos.FearAndGreed.builder()
                            .rating(fearAndGreed.get().getRating())
                            .score(fearAndGreed.get().getScore())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                log.error("insertCnt != 1");
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "20 9,19,29,39,49,59 * * * *")
    public void saveDJI() throws IOException {
        try (Response response = investingFeign.getDowJonesIndustrialAverage()) {
            Optional<EconomicIndex> dji = investingFeignReader.getEconomicIndex(response);

            if (dji.isEmpty()) {
                log.error("dji is Empty");
                return;
            }

            int insertCnt = djiRepository.insert(
                    Dji.builder()
                            .url(dji.get().getUrl())
                            .title(dji.get().getTitle())
                            .price(dji.get().getPrice())
                            .priceChange(dji.get().getPriceChange())
                            .priceChangePercent(dji.get().getPriceChangePercent())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                log.error("insertCnt != 1");
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "30 9,19,29,39,49,59 * * * *")
    public void saveSPX() throws IOException {
        try (Response response = investingFeign.getStandardAndPoor500()) {
            Optional<EconomicIndex> spx = investingFeignReader.getEconomicIndex(response);

            if (spx.isEmpty()) {
                log.error("spx is Empty");
                return;
            }

            int insertCnt = spxRepository.insert(
                    Spx.builder()
                            .url(spx.get().getUrl())
                            .title(spx.get().getTitle())
                            .price(spx.get().getPrice())
                            .priceChange(spx.get().getPriceChange())
                            .priceChangePercent(spx.get().getPriceChangePercent())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                log.error("insertCnt != 1");
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "40 9,19,29,39,49,59 * * * *")
    public void saveIXIC() throws IOException {
        try (Response response = investingFeign.getNasdaqComposite()) {
            Optional<EconomicIndex> ixic = investingFeignReader.getEconomicIndex(response);

            if (ixic.isEmpty()) {
                log.error("ixic is Empty");
                return;
            }

            int insertCnt = ixicRepository.insert(
                    Ixic.builder()
                            .url(ixic.get().getUrl())
                            .title(ixic.get().getTitle())
                            .price(ixic.get().getPrice())
                            .priceChange(ixic.get().getPriceChange())
                            .priceChangePercent(ixic.get().getPriceChangePercent())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                log.error("insertCnt != 1");
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "7 7 7 * * *")
    public void saveHoliday() throws Exception {
        try (Response response = investingFeign.getHolidayCalendar()) {
            holidayRepository.deleteAll();

            Map<String, Set<String>> holidayDateAndCountryListMap = investingFeignReader.getHolidayDateAndCountryListMap(response);

            if (holidayDateAndCountryListMap.isEmpty()) {
                throw new Exception("holidayDateAndCountryListMap is Empty");
            }

            for (Map.Entry<String, Set<String>> holidayDateAndCountrySetEntry : holidayDateAndCountryListMap.entrySet()) {
                LocalDate holidayDate = LocalDate.parse(holidayDateAndCountrySetEntry.getKey(), DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH));

                List<Holiday> holidayList = holidayDateAndCountrySetEntry.getValue()
                        .stream()
                        .map(country -> {
                            return Holiday.builder()
                                    .country(country)
                                    .date(holidayDate)
                                    .build();
                        })
                        .toList();

                int insertCnt = holidayRepository.insertBatch(
                        holidayList,
                        10,
                        Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName()
                );

                if (insertCnt != holidayList.size()) {
                    throw new Exception("insertCnt != holidayList.size()");
                }
            }
        }
    }
}
