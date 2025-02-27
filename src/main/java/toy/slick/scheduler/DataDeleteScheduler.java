package toy.slick.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Const;
import toy.slick.repository.mariadb.DjiRepository;
import toy.slick.repository.mariadb.EconomicEventRepository;
import toy.slick.repository.mariadb.FearAndGreedRepository;
import toy.slick.repository.mariadb.IxicRepository;
import toy.slick.repository.mariadb.KosdaqRepository;
import toy.slick.repository.mariadb.KospiRepository;
import toy.slick.repository.mariadb.SpxRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Component
@EnableAsync
@EnableScheduling
public class DataDeleteScheduler {
    /* Repository */
    private final EconomicEventRepository economicEventRepository;
    private final FearAndGreedRepository fearAndGreedRepository;
    private final DjiRepository djiRepository;
    private final IxicRepository ixicRepository;
    private final SpxRepository spxRepository;
    private final KospiRepository kospiRepository;
    private final KosdaqRepository kosdaqRepository;

    public DataDeleteScheduler(EconomicEventRepository economicEventRepository,
                               FearAndGreedRepository fearAndGreedRepository,
                               DjiRepository djiRepository,
                               IxicRepository ixicRepository,
                               SpxRepository spxRepository,
                               KospiRepository kospiRepository,
                               KosdaqRepository kosdaqRepository) {
        this.economicEventRepository = economicEventRepository;
        this.fearAndGreedRepository = fearAndGreedRepository;
        this.djiRepository = djiRepository;
        this.ixicRepository = ixicRepository;

        this.spxRepository = spxRepository;
        this.kospiRepository = kospiRepository;
        this.kosdaqRepository = kosdaqRepository;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 4 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteFearAndGreed() {
        LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .minusDays(2)
                .toLocalDateTime();

        int deleteCnt = fearAndGreedRepository.delete(untilDateTime);

        log.info("deleteCnt : " + deleteCnt);
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 14 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteDji() {
        LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .minusDays(2)
                .toLocalDateTime();

        int deleteCnt = djiRepository.delete(untilDateTime);

        log.info("deleteCnt : " + deleteCnt);
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 24 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteIxic() {
        LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .minusDays(2)
                .toLocalDateTime();

        int deleteCnt = ixicRepository.delete(untilDateTime);

        log.info("deleteCnt : " + deleteCnt);
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 34 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteSpx() {
        LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .minusDays(2)
                .toLocalDateTime();

        int deleteCnt = spxRepository.delete(untilDateTime);

        log.info("deleteCnt : " + deleteCnt);
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 44 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteKospi() {
        LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .minusDays(2)
                .toLocalDateTime();

        int deleteCnt = kospiRepository.delete(untilDateTime);

        log.info("deleteCnt : " + deleteCnt);
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 54 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteKosdaq() {
        LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .minusDays(2)
                .toLocalDateTime();

        int deleteCnt = kosdaqRepository.delete(untilDateTime);

        log.info("deleteCnt : " + deleteCnt);
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 5 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteEconomicEvent() {
        LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .minusDays(7)
                .toLocalDateTime();

        int deleteCnt = economicEventRepository.delete(untilDateTime);

        log.info("deleteCnt : " + deleteCnt);
    }
}
