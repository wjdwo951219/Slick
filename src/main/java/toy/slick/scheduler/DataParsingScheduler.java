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
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Const;
import toy.slick.exception.EmptyException;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class DataParsingScheduler {
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

    public DataParsingScheduler(CnnFeign cnnFeign,
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
    @Scheduled(cron = "50 9,19,29,39,49,59 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveImportantEconomicEventList() {
        try (Response response = economicCalendarFeign.getEconomicCalendar()) {
            List<EconomicEvent> economicEventList = economicCalendarFeignReader.getEconomicEventList(response);

            if (CollectionUtils.isEmpty(economicEventList)) {
                throw new EmptyException();
            }

            economicEventList
                    .stream()
                    .parallel()
                    .filter(o -> !StringUtils.equals("Low", o.getImportance()))
                    .forEach(o -> economicEventRepository.save(o.getId(),
                            o.getZonedDateTime().toLocalDateTime(),
                            o.getName(),
                            o.getCountry(),
                            o.getImportance(),
                            o.getActual(),
                            o.getForecast(),
                            o.getPrevious(),
                            Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName(),
                            Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "10 9,19,29,39,49,59 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveFearAndGreed() {
        try (Response response = cnnFeign.getFearAndGreed()) {
            Optional<FearAndGreed> fearAndGreed = cnnFeignReader.getFearAndGreed(response);

            if (fearAndGreed.isEmpty()) {
                throw new EmptyException(); // TODO: Exception message -> property
            }

            fearAndGreedRepository.insert(fearAndGreed.get().getRating(),
                    fearAndGreed.get().getScore(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "20 9,19,29,39,49,59 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveDJI() {
        try (Response response = investingFeign.getDowJonesIndustrialAverage()) {
            Optional<EconomicIndex> dji = investingFeignReader.getEconomicIndex(response);

            if (dji.isEmpty()) {
                throw new EmptyException();
            }

            economicIndexRepository.save(Const.EconomicIndex.DJI.getCode(),
                    dji.get().getUrl(),
                    dji.get().getTitle(),
                    dji.get().getPrice(),
                    dji.get().getPriceChange(),
                    dji.get().getPriceChangePercent(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "30 9,19,29,39,49,59 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveSPX() {
        try (Response response = investingFeign.getStandardAndPoor500()) {
            Optional<EconomicIndex> spx = investingFeignReader.getEconomicIndex(response);

            if (spx.isEmpty()) {
                throw new EmptyException();
            }

            economicIndexRepository.save(Const.EconomicIndex.SPX.getCode(),
                    spx.get().getUrl(),
                    spx.get().getTitle(),
                    spx.get().getPrice(),
                    spx.get().getPriceChange(),
                    spx.get().getPriceChangePercent(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "40 9,19,29,39,49,59 * * * *", zone = Const.ZoneId.NEW_YORK)
    public void saveIXIC() {
        try (Response response = investingFeign.getNasdaqComposite()) {
            Optional<EconomicIndex> ixic = investingFeignReader.getEconomicIndex(response);

            if (ixic.isEmpty()) {
                throw new EmptyException();
            }

            economicIndexRepository.save(Const.EconomicIndex.IXIC.getCode(),
                    ixic.get().getUrl(),
                    ixic.get().getTitle(),
                    ixic.get().getPrice(),
                    ixic.get().getPriceChange(),
                    ixic.get().getPriceChangePercent(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName(),
                    Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
