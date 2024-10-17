package toy.slick.common;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ObjectMapper {
    private ObjectMapper() {
    }

    private static class SingletonHelper {
        private static final com.fasterxml.jackson.databind.ObjectMapper objectMapper
                = new com.fasterxml.jackson.databind.ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module());

        private static final com.fasterxml.jackson.databind.ObjectMapper BILL_PUGH_SINGLETON_INSTANCE
                = objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
    }

    public static com.fasterxml.jackson.databind.ObjectMapper getInstance() {
        return SingletonHelper.BILL_PUGH_SINGLETON_INSTANCE;
    }

}
