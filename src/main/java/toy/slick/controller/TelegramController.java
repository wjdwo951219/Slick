package toy.slick.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import toy.slick.common.Const;
import toy.slick.scheduler.TelegramScheduler_KR;
import toy.slick.scheduler.TelegramScheduler_US;

@RestController
@RequestMapping("/telegram")
public class TelegramController {
    private final String CONTROLLER_KEY;
    private final TelegramScheduler_KR telegramScheduler_KR;
    private final TelegramScheduler_US telegramScheduler_US;

    public TelegramController(@Value("${controller.key}") String CONTROLLER_KEY,
                              TelegramScheduler_KR telegramScheduler_KR,
                              TelegramScheduler_US telegramScheduler_US) {
        this.CONTROLLER_KEY = CONTROLLER_KEY;
        this.telegramScheduler_KR = telegramScheduler_KR;
        this.telegramScheduler_US = telegramScheduler_US;
    }

    @GetMapping("/sendEconomicEventList/{channel}")
    public String sendEconomicEventList(
            @RequestHeader @NotBlank String key,
            @PathVariable @NotBlank String channel) throws Exception {
        if (!CONTROLLER_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        switch (channel) {
            case Const.Country.KR -> telegramScheduler_KR.sendEconomicEventList();
            case Const.Country.US -> telegramScheduler_US.sendEconomicEventList();
        }

        return HttpStatus.OK.toString();
    }

    @GetMapping("/sendIndices/{channel}")
    public String sendIndices(
            @RequestHeader @NotBlank String key,
            @PathVariable @NotBlank String channel) throws Exception {
        if (!CONTROLLER_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        switch (channel) {
            case Const.Country.KR -> telegramScheduler_KR.sendIndices();
            case Const.Country.US -> telegramScheduler_US.sendIndices();
        }

        return HttpStatus.OK.toString();
    }

    @GetMapping("/sendFearAndGreed")
    public String sendFearAndGreed(
            @RequestHeader @NotBlank String key) throws Exception {
        if (!CONTROLLER_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        telegramScheduler_US.sendFearAndGreed();

        return HttpStatus.OK.toString();
    }

    @GetMapping("/sendCurrencies/{channel}")
    public String sendFearAndGreed(
            @RequestHeader @NotBlank String key,
            @PathVariable @NotBlank String channel) throws Exception {
        if (!CONTROLLER_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        switch (channel) {
            case Const.Country.KR -> telegramScheduler_KR.sendCurrencies();
            // TODO : add other countries
        }

        return HttpStatus.OK.toString();
    }
}