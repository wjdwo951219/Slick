package toy.slick.feign.naver;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "NaverFeign", url = "https://m.stock.naver.com")
public interface NaverFeign {

    @GetMapping(value = "/front-api/marketIndex/productDetail?category=exchange&reutersCode=FX_USDKRW")
    Response getCurrencyUsdKrw();

    @GetMapping(value = "/front-api/marketIndex/productDetail?category=exchange&reutersCode=FX_JPYKRW")
    Response getCurrencyJpyKrw();

    @GetMapping(value = "/front-api/marketIndex/productDetail?category=exchange&reutersCode=FX_EURKRW")
    Response getCurrencyEurKrw();
}
