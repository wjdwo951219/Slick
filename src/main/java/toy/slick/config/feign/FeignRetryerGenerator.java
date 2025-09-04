package toy.slick.config.feign;

import feign.Request;
import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class FeignRetryerGenerator {

    public static Retryer create(Duration retryPeriod, Duration retryMaxPeriod, int retryCnt) {
        return new Retryer.Default(retryPeriod.toMillis(), retryMaxPeriod.toMillis(), retryCnt + 1) {
            @Override
            public void continueOrPropagate(RetryableException e) {
                if (Request.HttpMethod.GET == e.method()) {
                    log.info("Retrying : method={}, url={}, reason={}",
                            e.method(),
                            e.hasRequest() ? e.request().url() : null,
                            e.getMessage());

                    super.continueOrPropagate(e);
                } else {
                    throw e;
                }
            }
        };
    }

    public static Retryer create(Duration retryPeriod, int retryCnt) {
        return create(retryPeriod, retryPeriod, retryCnt);
    }
}
