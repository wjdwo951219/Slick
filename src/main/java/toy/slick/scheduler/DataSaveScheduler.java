package toy.slick.scheduler;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.generated.tables.pojos.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.MsgUtils;
import toy.slick.feign.cnn.CnnFeign;
import toy.slick.feign.cnn.reader.CnnFeignReader;
import toy.slick.feign.cnn.vo.response.FearAndGreed;
import toy.slick.feign.economicCalendar.EconomicCalendarFeign;
import toy.slick.feign.economicCalendar.reader.EconomicCalendarFeignReader;
import toy.slick.feign.economicCalendar.vo.response.EconomicEvent;
import toy.slick.feign.investing.InvestingFeign;
import toy.slick.feign.investing.reader.InvestingFeignReader;
import toy.slick.feign.investing.vo.response.EconomicIndex;
import toy.slick.repository.mysql.*;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
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
    private final KospiRepository kospiRepository;
    private final KosdaqRepository kosdaqRepository;
    private final CurrencyUsdKrwRepository currencyUsdKrwRepository;
    private final CurrencyJpyKrwRepository currencyJpyKrwRepository;
    private final CurrencyEurKrwRepository currencyEurKrwRepository;

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
                             KospiRepository kospiRepository,
                             KosdaqRepository kosdaqRepository,
                             CurrencyUsdKrwRepository currencyUsdKrwRepository,
                             CurrencyJpyKrwRepository currencyJpyKrwRepository,
                             CurrencyEurKrwRepository currencyEurKrwRepository) {
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
        this.kospiRepository = kospiRepository;
        this.kosdaqRepository = kosdaqRepository;
        this.currencyUsdKrwRepository = currencyUsdKrwRepository;
        this.currencyJpyKrwRepository = currencyJpyKrwRepository;
        this.currencyEurKrwRepository = currencyEurKrwRepository;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "8 8/10 * * * *")
    public void saveEconomicEventList() throws Exception {
        try (Response response = economicCalendarFeign.getEconomicCalendar()) {
            Map<String, EconomicEvent> economicEventMap = economicCalendarFeignReader.getEconomicEventList(response)
                    .stream()
                    .filter(o -> StringUtils.isNotBlank(o.getActual()))
                    .sorted(Comparator.comparing(EconomicEvent::getZonedDateTime))
                    .collect(Collectors.toMap(EconomicEvent::getId, Function.identity(), (o1, o2) -> o2));

            if (CollectionUtils.isEmpty(economicEventMap.keySet())) {
                return;
            }

            int deleteCnt = economicEventRepository.delete(economicEventMap.keySet());

            log.info(MsgUtils.deleteCntMsg(deleteCnt));

            int insertCnt = economicEventRepository.insertBatch(
                    economicEventMap.values()
                            .stream()
                            .map(o -> org.jooq.generated.tables.pojos.EconomicEvent.builder()
                                    .id(o.getId())
                                    .url(o.getUrl())
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

            if (insertCnt != economicEventMap.size()) {
                throw new Exception(MsgUtils.insertCntMsg(insertCnt, economicEventMap.size()));
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "10 9/10 * * * *")
    public void saveFearAndGreed() throws Exception {
        try (Response response = cnnFeign.getFearAndGreed()) {
            Optional<FearAndGreed> fearAndGreed = cnnFeignReader.getFearAndGreed(response);

            if (fearAndGreed.isEmpty()) {
                throw new Exception(MsgUtils.emptyMsg(fearAndGreed));
            }

            int insertCnt = fearAndGreedRepository.insert(
                    org.jooq.generated.tables.pojos.FearAndGreed.builder()
                            .rating(fearAndGreed.get().getRating())
                            .score(fearAndGreed.get().getScore())
                            .datetime(fearAndGreed.get().getUtcDateTime())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                throw new Exception(MsgUtils.insertCntMsg(insertCnt, 1));
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "20 9/10 * * * *")
    public void saveDji() throws Exception {
        try (Response response = investingFeign.getDowJonesIndustrialAverage()) {
            Optional<EconomicIndex> dji = investingFeignReader.getEconomicIndex(response);

            if (dji.isEmpty()) {
                throw new Exception(MsgUtils.emptyMsg(dji));
            }

            int insertCnt = djiRepository.insert(
                    Dji.builder()
                            .url(dji.get().getUrl())
                            .title(dji.get().getTitle())
                            .price(dji.get().getPrice())
                            .priceChange(dji.get().getPriceChange())
                            .priceChangePercent(dji.get().getPriceChangePercent())
                            .datetime(dji.get().getUtcDateTime())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                throw new Exception(MsgUtils.insertCntMsg(insertCnt, 1));
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "30 9/10 * * * *")
    public void saveSpx() throws Exception {
        try (Response response = investingFeign.getStandardAndPoor500()) {
            Optional<EconomicIndex> spx = investingFeignReader.getEconomicIndex(response);

            if (spx.isEmpty()) {
                throw new Exception(MsgUtils.emptyMsg(spx));
            }

            int insertCnt = spxRepository.insert(
                    Spx.builder()
                            .url(spx.get().getUrl())
                            .title(spx.get().getTitle())
                            .price(spx.get().getPrice())
                            .priceChange(spx.get().getPriceChange())
                            .priceChangePercent(spx.get().getPriceChangePercent())
                            .datetime(spx.get().getUtcDateTime())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                throw new Exception(MsgUtils.insertCntMsg(insertCnt, 1));
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "40 9/10 * * * *")
    public void saveIxic() throws Exception {
        try (Response response = investingFeign.getNasdaqComposite()) {
            Optional<EconomicIndex> ixic = investingFeignReader.getEconomicIndex(response);

            if (ixic.isEmpty()) {
                throw new Exception(MsgUtils.emptyMsg(ixic));
            }

            int insertCnt = ixicRepository.insert(
                    Ixic.builder()
                            .url(ixic.get().getUrl())
                            .title(ixic.get().getTitle())
                            .price(ixic.get().getPrice())
                            .priceChange(ixic.get().getPriceChange())
                            .priceChangePercent(ixic.get().getPriceChangePercent())
                            .datetime(ixic.get().getUtcDateTime())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                throw new Exception(MsgUtils.insertCntMsg(insertCnt, 1));
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "30 8/10 * * * *")
    public void saveKospi() throws Exception {
        try (Response response = investingFeign.getKospi()) {
            Optional<EconomicIndex> kospi = investingFeignReader.getEconomicIndex(response);

            if (kospi.isEmpty()) {
                throw new Exception(MsgUtils.emptyMsg(kospi));
            }

            int insertCnt = kospiRepository.insert(
                    Kospi.builder()
                            .url(kospi.get().getUrl())
                            .title(kospi.get().getTitle())
                            .price(kospi.get().getPrice())
                            .priceChange(kospi.get().getPriceChange())
                            .priceChangePercent(kospi.get().getPriceChangePercent())
                            .datetime(kospi.get().getUtcDateTime())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                throw new Exception(MsgUtils.insertCntMsg(insertCnt, 1));
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "40 8/10 * * * *")
    public void saveKosdaq() throws Exception {
        try (Response response = investingFeign.getKosdaq()) {
            Optional<EconomicIndex> kosdaq = investingFeignReader.getEconomicIndex(response);

            if (kosdaq.isEmpty()) {
                throw new Exception(MsgUtils.emptyMsg(kosdaq));
            }

            int insertCnt = kosdaqRepository.insert(
                    Kosdaq.builder()
                            .url(kosdaq.get().getUrl())
                            .title(kosdaq.get().getTitle())
                            .price(kosdaq.get().getPrice())
                            .priceChange(kosdaq.get().getPriceChange())
                            .priceChangePercent(kosdaq.get().getPriceChangePercent())
                            .datetime(kosdaq.get().getUtcDateTime())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                throw new Exception(MsgUtils.insertCntMsg(insertCnt, 1));
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "45 4/5 * * * *")
    public void saveCurrencyUsdKrw() throws Exception {
        try (Response response = investingFeign.getCurrenciesUsdKrw()) {
            Optional<EconomicIndex> currency = investingFeignReader.getEconomicIndex(response);

            if (currency.isEmpty()) {
                throw new Exception(MsgUtils.emptyMsg(currency));
            }

            int insertCnt = currencyUsdKrwRepository.insert(
                    CurrencyUsdKrw.builder()
                            .price(currency.get().getPrice())
                            .priceChange(currency.get().getPriceChange())
                            .priceChangePercent(currency.get().getPriceChangePercent())
                            .datetime(currency.get().getUtcDateTime())
                            .url(currency.get().getUrl())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                throw new Exception(MsgUtils.insertCntMsg(insertCnt, 1));
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "40 4/5 * * * *")
    public void saveCurrencyJpyKrw() throws Exception {
        try (Response response = investingFeign.getCurrenciesJpyKrw()) {
            Optional<EconomicIndex> currency = investingFeignReader.getEconomicIndex(response);

            if (currency.isEmpty()) {
                throw new Exception(MsgUtils.emptyMsg(currency));
            }

            int insertCnt = currencyJpyKrwRepository.insert(
                    CurrencyJpyKrw.builder()
                            .price(currency.get().getPrice())
                            .priceChange(currency.get().getPriceChange())
                            .priceChangePercent(currency.get().getPriceChangePercent())
                            .datetime(currency.get().getUtcDateTime())
                            .url(currency.get().getUrl())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                throw new Exception(MsgUtils.insertCntMsg(insertCnt, 1));
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "35 4/5 * * * *")
    public void saveCurrencyEurKrw() throws Exception {
        try (Response response = investingFeign.getCurrenciesEurKrw()) {
            Optional<EconomicIndex> currency = investingFeignReader.getEconomicIndex(response);

            if (currency.isEmpty()) {
                throw new Exception(MsgUtils.emptyMsg(currency));
            }

            int insertCnt = currencyEurKrwRepository.insert(
                    CurrencyEurKrw.builder()
                            .price(currency.get().getPrice())
                            .priceChange(currency.get().getPriceChange())
                            .priceChangePercent(currency.get().getPriceChangePercent())
                            .datetime(currency.get().getUtcDateTime())
                            .url(currency.get().getUrl())
                            .build(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

            if (insertCnt != 1) {
                throw new Exception(MsgUtils.insertCntMsg(insertCnt, 1));
            }
        }
    }
}
