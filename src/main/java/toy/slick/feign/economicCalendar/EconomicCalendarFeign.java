package toy.slick.feign.economicCalendar;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "EconomicCalendarFeign", url = "https://tradingeconomics.com/calendar")
public interface EconomicCalendarFeign {

    @GetMapping("")
    Response getEconomicCalendar();
}
