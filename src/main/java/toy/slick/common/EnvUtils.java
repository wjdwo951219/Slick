package toy.slick.common;

public class EnvUtils {
    private EnvUtils() {
    }

    public static boolean isProd(String SPRING_PROFILES_ACTIVE) {
        return "prod".equals(SPRING_PROFILES_ACTIVE);
    }
}
