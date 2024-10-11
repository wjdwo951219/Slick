package toy.slick.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Response;
import toy.slick.controller.vo.response.FearAndGreedRes;
import toy.slick.service.cacheable.EconomicInfoCacheableService;

@Slf4j
@RestController
@RequestMapping("/api/economicInfo")
public class EconomicInfoController {
    private final EconomicInfoCacheableService economicInfoCacheableService;

    public EconomicInfoController(EconomicInfoCacheableService economicInfoCacheableService) {
        this.economicInfoCacheableService = economicInfoCacheableService;
    }

    @TimeLogAspect.TimeLog
    @GetMapping("/fearAndGreed")
    public Response<FearAndGreedRes> getFearAndGreed() {
        return new Response<>(economicInfoCacheableService.getRecentFearAndGreed());
    }
}
