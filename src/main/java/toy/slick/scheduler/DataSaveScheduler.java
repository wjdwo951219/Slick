package toy.slick.scheduler;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Const;
import toy.slick.exception.EmptyException;
import toy.slick.exception.QueryResultCntException;
import toy.slick.feign.cnn.CnnFeign;
import toy.slick.feign.cnn.reader.CnnFeignReader;
import toy.slick.feign.cnn.vo.response.FearAndGreed;
import toy.slick.feign.economicCalendar.EconomicCalendarFeign;
import toy.slick.feign.economicCalendar.reader.EconomicCalendarFeignReader;
import toy.slick.feign.economicCalendar.vo.response.EconomicEvent;
import toy.slick.feign.investing.InvestingFeign;
import toy.slick.feign.investing.reader.InvestingFeignReader;
import toy.slick.feign.investing.vo.response.EconomicIndex;
import toy.slick.repository.mariadb.EconomicEventRepository;
import toy.slick.repository.mariadb.EconomicIndexRepository;
import toy.slick.repository.mariadb.FearAndGreedRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
    private final EconomicIndexRepository economicIndexRepository;

    public DataSaveScheduler(CnnFeign cnnFeign,
                             EconomicCalendarFeign economicCalendarFeign,
                             InvestingFeign investingFeign,
                             CnnFeignReader cnnFeignReader,
                             EconomicCalendarFeignReader economicCalendarFeignReader,
                             InvestingFeignReader investingFeignReader,
                             EconomicEventRepository economicEventRepository,
                             FearAndGreedRepository fearAndGreedRepository,
                             EconomicIndexRepository economicIndexRepository) {
        this.cnnFeign = cnnFeign;
        this.economicCalendarFeign = economicCalendarFeign;
        this.investingFeign = investingFeign;
        this.cnnFeignReader = cnnFeignReader;
        this.economicCalendarFeignReader = economicCalendarFeignReader;
        this.investingFeignReader = investingFeignReader;
        this.economicEventRepository = economicEventRepository;
        this.fearAndGreedRepository = fearAndGreedRepository;
        this.economicIndexRepository = economicIndexRepository;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "50 9,19,29,39,49,59 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveImportantEconomicEventList() throws IOException {
        try (Response response = economicCalendarFeign.getEconomicCalendar()) {
            List<EconomicEvent> economicEventList = economicCalendarFeignReader.getEconomicEventList(response)
                    .stream()
                    .filter(o -> !StringUtils.equals("Low", o.getImportance()))
                    .filter(o -> StringUtils.isNotBlank(o.getActual()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(economicEventList)) {
                throw new EmptyException("economicEventList is Empty");
            }

            economicEventRepository.delete(economicEventList
                    .stream()
                    .map(EconomicEvent::getId)
                    .collect(Collectors.toSet()));

            int insertCnt = economicEventRepository.insertBatch(economicEventList
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
                            .regId(Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())
                            .uptId(Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())
                            .build())
                    .collect(Collectors.toList()), 10);

            if (insertCnt != economicEventList.size()) {
                throw new QueryResultCntException("insertCnt != economicEventList.size()");
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "10 9,19,29,39,49,59 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveFearAndGreed() throws IOException {
        try (Response response = cnnFeign.getFearAndGreed()) {
            Optional<FearAndGreed> fearAndGreed = cnnFeignReader.getFearAndGreed(response);

            if (fearAndGreed.isEmpty()) {
                throw new EmptyException("fearAndGreed is Empty"); // TODO: Exception message -> property
            }

            int insertCnt = fearAndGreedRepository.insert(org.jooq.generated.tables.pojos.FearAndGreed.builder()
                    .rating(fearAndGreed.get().getRating())
                    .score(fearAndGreed.get().getScore())
                    .regId(Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())
                    .uptId(Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())
                    .build());

            if (insertCnt != 1) {
                throw new QueryResultCntException("insertCnt != 1");
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "20 9,19,29,39,49,59 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveDJI() throws IOException {
        try (Response response = investingFeign.getDowJonesIndustrialAverage()) {
            Optional<EconomicIndex> dji = investingFeignReader.getEconomicIndex(response);

            if (dji.isEmpty()) {
                throw new EmptyException("dji is Empty");
            }

            int deleteCnt = economicIndexRepository.delete(Const.EconomicIndex.DJI.getCode());

            if (deleteCnt > 1) {
                throw new QueryResultCntException("deleteCnt > 1");
            }

            int insertCnt = economicIndexRepository.insert(org.jooq.generated.tables.pojos.EconomicIndex.builder()
                    .code(Const.EconomicIndex.DJI.getCode())
                    .url(dji.get().getUrl())
                    .title(dji.get().getTitle())
                    .price(dji.get().getPrice())
                    .priceChange(dji.get().getPriceChange())
                    .priceChangePercent(dji.get().getPriceChangePercent())
                    .regId(Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())
                    .uptId(Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())
                    .build());

            if (insertCnt != 1) {
                throw new QueryResultCntException("insertCnt != 1");
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "30 9,19,29,39,49,59 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveSPX() throws IOException {
        try (Response response = investingFeign.getStandardAndPoor500()) {
            Optional<EconomicIndex> spx = investingFeignReader.getEconomicIndex(response);

            if (spx.isEmpty()) {
                throw new EmptyException("spx is Empty");
            }

            int deleteCnt = economicIndexRepository.delete(Const.EconomicIndex.SPX.getCode());

            if (deleteCnt > 1) {
                throw new QueryResultCntException("deleteCnt > 1");
            }

            int insertCnt = economicIndexRepository.insert(org.jooq.generated.tables.pojos.EconomicIndex.builder()
                    .code(Const.EconomicIndex.SPX.getCode())
                    .url(spx.get().getUrl())
                    .title(spx.get().getTitle())
                    .price(spx.get().getPrice())
                    .priceChange(spx.get().getPriceChange())
                    .priceChangePercent(spx.get().getPriceChangePercent())
                    .regId(Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())
                    .uptId(Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())
                    .build());

            if (insertCnt != 1) {
                throw new QueryResultCntException("insertCnt != 1");
            }
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Transactional
    @Scheduled(cron = "40 9,19,29,39,49,59 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveIXIC() throws IOException {
        try (Response response = investingFeign.getNasdaqComposite()) {
            Optional<EconomicIndex> ixic = investingFeignReader.getEconomicIndex(response);

            if (ixic.isEmpty()) {
                throw new EmptyException("ixic is Empty");
            }

            int deleteCnt = economicIndexRepository.delete(Const.EconomicIndex.IXIC.getCode());

            if (deleteCnt > 1) {
                throw new QueryResultCntException("deleteCnt > 1");
            }

            int insertCnt = economicIndexRepository.insert(org.jooq.generated.tables.pojos.EconomicIndex.builder()
                    .code(Const.EconomicIndex.IXIC.getCode())
                    .url(ixic.get().getUrl())
                    .title(ixic.get().getTitle())
                    .price(ixic.get().getPrice())
                    .priceChange(ixic.get().getPriceChange())
                    .priceChangePercent(ixic.get().getPriceChangePercent())
                    .regId(Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())
                    .uptId(Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())
                    .build());

            if (insertCnt != 1) {
                throw new QueryResultCntException("insertCnt != 1");
            }
        }
    }
}
