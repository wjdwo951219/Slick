package toy.slick.config.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.AppenderBase;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import toy.slick.repository.mongodb.LogRepository;

@Component
public class MongoDBAppender extends AppenderBase<ILoggingEvent> implements ApplicationContextAware {
    private static LogRepository logRepository;

    @Override
    protected void append(ILoggingEvent eventObject) {
        StackTraceElement[] callerData = eventObject.getCallerData();
        StackTraceElement stackTraceElement = callerData[0];
        String threadName = eventObject.getThreadName();
        String level = eventObject.getLevel().toString();
        String logger = eventObject.getLoggerName();
        String msg = eventObject.getFormattedMessage();
        String className = stackTraceElement.getClassName();
        String method = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();

        String log = String.format("[%s] %s %s.%s.%s:%d - %s\n", threadName, level, logger, className, method, lineNumber, msg);

        if (eventObject.getThrowableProxy() != null) {
            log += ThrowableProxyUtil.asString(eventObject.getThrowableProxy());
        }

        logRepository.save(LogRepository.Log.builder()
                .log(log)
                .build()
                .toMongoData(String.valueOf(System.currentTimeMillis())));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logRepository = applicationContext.getAutowireCapableBeanFactory().getBean(LogRepository.class);
    }
}
