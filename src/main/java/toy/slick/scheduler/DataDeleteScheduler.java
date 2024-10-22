package toy.slick.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Const;
import toy.slick.repository.mariadb.EconomicEventRepository;
import toy.slick.repository.mariadb.FearAndGreedRepository;
import toy.slick.repository.mariadb.SignUpReqRepository;

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
    private final SignUpReqRepository signUpReqRepository;

    public DataDeleteScheduler(EconomicEventRepository economicEventRepository,
                               FearAndGreedRepository fearAndGreedRepository,
                               SignUpReqRepository signUpReqRepository) {
        this.economicEventRepository = economicEventRepository;
        this.fearAndGreedRepository = fearAndGreedRepository;
        this.signUpReqRepository = signUpReqRepository;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 4 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteFearAndGreed() {
        try {
            LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                    .minusDays(2)
                    .toLocalDateTime();

            int deleteCnt = fearAndGreedRepository.delete(untilDateTime);

            log.info("deleteCnt : " + deleteCnt);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 5 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteEconomicEvent() {
        try {
            LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                    .minusDays(7)
                    .toLocalDateTime();

            int deleteCnt = economicEventRepository.delete(untilDateTime);

            log.info("deleteCnt : " + deleteCnt);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 * * * * *", zone = Const.ZoneId.UTC)
    public void deleteSignUpReq() {
        try {
            LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                    .minusMinutes(5)
                    .toLocalDateTime();

            int deleteCnt = signUpReqRepository.delete(untilDateTime);

            log.info("deleteCnt : " + deleteCnt);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
