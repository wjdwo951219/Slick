package toy.slick.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Const;
import toy.slick.common.MsgUtils;
import toy.slick.repository.mysql.CurrencyEurKrwRepository;
import toy.slick.repository.mysql.CurrencyJpyKrwRepository;
import toy.slick.repository.mysql.CurrencyUsdKrwRepository;
import toy.slick.repository.mysql.DjiRepository;
import toy.slick.repository.mysql.EconomicEventRepository;
import toy.slick.repository.mysql.FearAndGreedRepository;
import toy.slick.repository.mysql.IxicRepository;
import toy.slick.repository.mysql.KosdaqRepository;
import toy.slick.repository.mysql.KospiRepository;
import toy.slick.repository.mysql.SpxRepository;

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
    private final CurrencyEurKrwRepository currencyEurKrwRepository;
    private final CurrencyJpyKrwRepository currencyJpyKrwRepository;
    private final CurrencyUsdKrwRepository currencyUsdKrwRepository;

    public DataDeleteScheduler(EconomicEventRepository economicEventRepository,
                               FearAndGreedRepository fearAndGreedRepository,
                               DjiRepository djiRepository,
                               IxicRepository ixicRepository,
                               SpxRepository spxRepository,
                               KospiRepository kospiRepository,
                               KosdaqRepository kosdaqRepository,
                               CurrencyEurKrwRepository currencyEurKrwRepository,
                               CurrencyJpyKrwRepository currencyJpyKrwRepository,
                               CurrencyUsdKrwRepository currencyUsdKrwRepository) {
        this.economicEventRepository = economicEventRepository;
        this.fearAndGreedRepository = fearAndGreedRepository;
        this.djiRepository = djiRepository;
        this.ixicRepository = ixicRepository;
        this.spxRepository = spxRepository;
        this.kospiRepository = kospiRepository;
        this.kosdaqRepository = kosdaqRepository;
        this.currencyEurKrwRepository = currencyEurKrwRepository;
        this.currencyJpyKrwRepository = currencyJpyKrwRepository;
        this.currencyUsdKrwRepository = currencyUsdKrwRepository;
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 44 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteEconomicIndices() {
        LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .minusDays(2)
                .toLocalDateTime();

        log.info(MsgUtils.deleteCntMsg(kosdaqRepository.delete(untilDateTime)));

        log.info(MsgUtils.deleteCntMsg(kospiRepository.delete(untilDateTime)));

        log.info(MsgUtils.deleteCntMsg(spxRepository.delete(untilDateTime)));

        log.info(MsgUtils.deleteCntMsg(ixicRepository.delete(untilDateTime)));

        log.info(MsgUtils.deleteCntMsg(djiRepository.delete(untilDateTime)));

        log.info(MsgUtils.deleteCntMsg(fearAndGreedRepository.delete(untilDateTime)));
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 5 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteEconomicEvent() {
        LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .minusDays(7)
                .toLocalDateTime();

        int deleteCnt = economicEventRepository.delete(untilDateTime);

        log.info(MsgUtils.deleteCntMsg(deleteCnt));
    }

    @TimeLogAspect.TimeLog
    @Async
    @Scheduled(cron = "4 14 4 * * *", zone = Const.ZoneId.SEOUL)
    public void deleteCurrencies() {
        LocalDateTime untilDateTime = ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .minusDays(2)
                .withHour(0)
                .toLocalDateTime();

        log.info(MsgUtils.deleteCntMsg(currencyEurKrwRepository.delete(untilDateTime)));

        log.info(MsgUtils.deleteCntMsg(currencyJpyKrwRepository.delete(untilDateTime)));

        log.info(MsgUtils.deleteCntMsg(currencyUsdKrwRepository.delete(untilDateTime)));
    }
}
