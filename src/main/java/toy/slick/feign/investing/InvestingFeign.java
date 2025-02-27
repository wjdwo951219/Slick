package toy.slick.feign.investing;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "InvestingFeign", url = "https://investing.com")
public interface InvestingFeign {

    @GetMapping(value = "/indices/us-30")
    Response getDowJonesIndustrialAverage();

    @GetMapping(value = "/indices/us-spx-500")
    Response getStandardAndPoor500();

    @GetMapping(value = "/indices/nasdaq-composite")
    Response getNasdaqComposite();

    @GetMapping(value = "/holiday-calendar", headers = {
            "accept-language=ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
            "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    })
    Response getHolidayCalendar();

    @GetMapping(value = "/indices/kospi")
    Response getKospi();

    @GetMapping(value = "/indices/kosdaq")
    Response getKosdaq();
}
