package toy.slick.controller;


import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.slick.common.Response;
import toy.slick.controller.vo.response.DeleteRes;
import toy.slick.interceptor.ApiKeyInterceptor;
import toy.slick.service.CacheService;

@Slf4j
@RestController
@RequestMapping("/api/cache")
public class CacheController {
    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @ApiKeyInterceptor.IsAdmin
    @DeleteMapping("/all")
    public Response<DeleteRes> delete() {
        return new Response<>(cacheService.delete());
    }

    @ApiKeyInterceptor.IsAdmin
    @DeleteMapping("/{cacheName}")
    public Response<DeleteRes> delete(@PathVariable @Parameter(example = "_1hour") String cacheName) {
        return new Response<>(cacheService.delete(cacheName));
    }

    @ApiKeyInterceptor.IsAdmin
    @DeleteMapping("/{cacheName}/{objectName}")
    public Response<DeleteRes> delete(@PathVariable @Parameter(example = "_1hour") String cacheName,
                                      @PathVariable @Parameter(example = "EconomicInfoCacheableService") String objectName) {
        return new Response<>(cacheService.delete(cacheName, objectName));
    }

    @ApiKeyInterceptor.IsAdmin
    @DeleteMapping("/{cacheName}/{objectName}/{methodName}")
    public Response<DeleteRes> delete(@PathVariable @Parameter(example = "_1hour") String cacheName,
                                      @PathVariable @Parameter(example = "EconomicInfoCacheableService") String objectName,
                                      @PathVariable @Parameter(example = "getApiKey") String methodName) {
        return new Response<>(cacheService.delete(cacheName, objectName, methodName));
    }
}
