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

    @GetMapping(value = "/indices/us-spx-500-technical", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getStandardAndPoor500();

    @GetMapping(value = "/indices/nasdaq-composite-technical", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getNasdaqComposite();

    @GetMapping(value = "/indices/kospi-technical", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getKospi();

    @GetMapping(value = "/indices/kosdaq-technical", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getKosdaq();

    @GetMapping(value = "/currencies/eur-krw-technical", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getCurrenciesEurKrw();

    @GetMapping(value = "/currencies/jpy-krw-technical", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getCurrenciesJpyKrw();

    @GetMapping(value = "/currencies/usd-krw-technical", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getCurrenciesUsdKrw();
}
