package toy.slick.feign.investing;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import toy.slick.common.Const;

@FeignClient(name = "InvestingFeign", url = "https://investing.com")
public interface InvestingFeign {

    @GetMapping(value = "/indices/us-30", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getDowJonesIndustrialAverage();

    @GetMapping(value = "/indices/us-spx-500", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getStandardAndPoor500();

    @GetMapping(value = "/indices/nasdaq-composite", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getNasdaqComposite();

    @GetMapping(value = "/holiday-calendar", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getHolidayCalendar();

    @GetMapping(value = "/indices/kospi", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getKospi();

    @GetMapping(value = "/indices/kosdaq", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getKosdaq();
}
