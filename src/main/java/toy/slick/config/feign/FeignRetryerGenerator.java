package toy.slick.config.feign;

import feign.Request;
import feign.RetryableException;
import feign.Retryer;

import java.time.Duration;

public class FeignRetryerGenerator {

    public static Retryer create(Duration retryPeriod, Duration retryMaxPeriod, int retryCnt) {
        return new Retryer.Default(retryPeriod.toMillis(), retryMaxPeriod.toMillis(), retryCnt + 1) {
            @Override
            public void continueOrPropagate(RetryableException e) {
                if (Request.HttpMethod.GET == e.method()) {
                    super.continueOrPropagate(e);
                } else {
                    throw e;
                }
            }
        };
    }

    public static Retryer create(Duration retryPeriod, int retryCnt) {
        return new Retryer.Default(retryPeriod.toMillis(), retryPeriod.toMillis(), retryCnt + 1) {
            @Override
            public void continueOrPropagate(RetryableException e) {
                if (Request.HttpMethod.GET == e.method()) {
                    super.continueOrPropagate(e);
                } else {
                    throw e;
                }
            }
        };
    }
}
