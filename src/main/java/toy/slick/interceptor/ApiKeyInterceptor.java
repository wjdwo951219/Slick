package toy.slick.interceptor;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jooq.generated.tables.pojos.ApiKey;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import toy.slick.common.Const;
import toy.slick.exception.EmptyException;
import toy.slick.exception.ApiKeyException;
import toy.slick.exception.Bucket4jException;
import toy.slick.service.cacheable.UserCacheableService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {
    private final LettuceBasedProxyManager<String> proxyManager;
    private final UserCacheableService userCacheableService;

    public ApiKeyInterceptor(LettuceBasedProxyManager<String> proxyManager,
                             UserCacheableService userCacheableService) {
        this.proxyManager = proxyManager;
        this.userCacheableService = userCacheableService;
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface IsAdmin {
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        HttpSession session = request.getSession();

        if (session.getAttribute("email") == null) {
            throw new EmptyException("Not sign in. retry after sign in");
        }

        session.setMaxInactiveInterval(session.getMaxInactiveInterval());

        String requestApiKey = session.getAttribute("requestApiKey").toString();
        String email = session.getAttribute("email").toString();
        ApiKey apiKey = userCacheableService.getApiKey(requestApiKey, email);

        this.checkBucket4j(requestApiKey, Const.BucketLevel.valueOf(apiKey.getBucketLevel()).getBucketConfiguration());

        this.checkApiKey(apiKey);

        this.checkRole(apiKey.getRole(), handler);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private void checkApiKey(ApiKey apiKey) throws ApiKeyException {
        if (!"Y".equals(apiKey.getUseYn())) {
            throw new ApiKeyException("ApiKey is unusable");
        }

        if (ZonedDateTime.now(ZoneId.of(Const.ZoneId.UTC))
                .isAfter(apiKey.getExpiredDatetime().atZone(ZoneId.of(Const.ZoneId.UTC)))) {
            throw new ApiKeyException("ApiKey is expired");
        }
    }

    private void checkRole(String role, Object handler) throws ApiKeyException {
        // when 404 request, handler -> HandlerMethod casting exception
        if (handler instanceof HandlerMethod handlerMethod) {
            if (handlerMethod.getMethodAnnotation(IsAdmin.class) != null) {
                if (!Const.Role.ADMIN.getName().equals(role)) {
                    throw new ApiKeyException("ApiKey has not appropriate role");
                }
            }
        }
    }

    private void checkBucket4j(String requestApiKey, BucketConfiguration bucketConfiguration) throws Bucket4jException {
        String redisKey = "Bucket4j:" + requestApiKey;

        Bucket bucket = proxyManager.builder()
                .build(redisKey, () -> bucketConfiguration);

        if (!bucket.tryConsume(1)) {
            throw new Bucket4jException("bucket consume fail");
        }
    }
}
