package toy.slick.feign.cnn;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import toy.slick.common.Const;

@FeignClient(name = "CnnFeign", url = "https://production.dataviz.cnn.io")
public interface CnnFeign {

    @GetMapping(value = "/index/fearandgreed/graphdata", headers = {
            Const.HttpRequestHeader.ACCEPT_LANGUAGE,
            Const.HttpRequestHeader.USER_AGENT
    })
    Response getFearAndGreed();
}
