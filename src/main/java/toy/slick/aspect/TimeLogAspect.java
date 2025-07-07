package toy.slick.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import toy.slick.common.Const;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Aspect
@Component
public class TimeLogAspect {
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TimeLog {
    }

    @Before("@annotation(toy.slick.aspect.TimeLogAspect.TimeLog)")
    public void logStartTime(JoinPoint joinPoint) {
        log.info("{} start : {}", joinPoint.getSignature(), ZonedDateTime.now(ZoneId.of(Const.ZoneId.SEOUL)));
    }

    @After("@annotation(toy.slick.aspect.TimeLogAspect.TimeLog)")
    public void logEndTime(JoinPoint joinPoint) {
        log.info("{} end : {}", joinPoint.getSignature(), ZonedDateTime.now(ZoneId.of(Const.ZoneId.SEOUL)));
    }

    @Around("@annotation(toy.slick.aspect.TimeLogAspect.TimeLog)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long end = System.currentTimeMillis();

        log.info("{} execution time : {}ms", joinPoint.getSignature(), end - start);

        return proceed;
    }
}
