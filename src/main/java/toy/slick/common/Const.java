package toy.slick.common;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import lombok.Getter;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public interface Const {
    String FACE_SCREAMING_IN_FEAR = "\uD83D\uDE31"; // 😱
    String FEARFUL_FACE = "\uD83D\uDE28"; // 😨
    String THINKING_FACE = "\uD83E\uDD14"; // 🤔
    String GRINNING_SQUINTING_FACE = "\uD83D\uDE06"; // 😆
    String ZANY_FACE = "\uD83E\uDD2A"; // 🤪
    String FLAG = "\ud83d\udea9"; // 🚩
    String CHECK_MARK = "\u2705"; // ✅
    String DOWN_CHART = "\uD83D\uDCC9"; // 📉
    String UP_CHART = "\uD83D\uDCC8"; // 📈

    @Getter
    enum DateTimeFormat {
        yyyyMMdd("yyyyMMdd", DateTimeFormatter.ofPattern("yyyyMMdd")),
        yyyyMMdd_hyphen("yyyy-MM-dd", DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        yyyyMMddHH("yyyyMMddHH", DateTimeFormatter.ofPattern("yyyyMMddHH")),
        yyyyMMdd_DotBlank("yyyy. MM. dd.", DateTimeFormatter.ofPattern("yyyy. MM. dd.")),
        yyyyMMddHHmmss("yyyy-MM-dd HH:mm:ss", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        private final String format;
        private final DateTimeFormatter dateTimeFormatter;

        DateTimeFormat(String format, DateTimeFormatter dateTimeFormatter) {
            this.format = format;
            this.dateTimeFormatter = dateTimeFormatter;
        }
    }

    interface ZoneId {
        String NEW_YORK = "America/New_York";
        String SEOUL = "Asia/Seoul";
        String UTC = "UTC";
    }

    @Getter
    enum EconomicIndex {
        DJI("DJI"),
        SPX("SPX"),
        IXIC("IXIC");

        private final String code;

        EconomicIndex(String code) {
            this.code = code;
        }
    }

    @Getter
    enum Role {
        ADMIN("ADMIN"),
        USER("USER");

        private final String name;

        Role(String name) {
            this.name = name;
        }
    }

    @Getter
    enum BucketLevel {
        BASIC("BASIC", BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(10)
                        .refillIntervally(10, Duration.ofMinutes(30))
                        .build())
                .build()),
        STANDARD("STANDARD", BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(60)
                        .refillIntervally(60, Duration.ofMinutes(30))
                        .build())
                .build()),
        PREMIUM("PREMIUM", BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(60)
                        .refillIntervally(60, Duration.ofMinutes(10))
                        .build())
                .build()),
        INFINITE("INFINITE", BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(30)
                        .refillIntervally(30, Duration.ofSeconds(10))
                        .build())
                .build());

        private final String levelName;
        private final BucketConfiguration bucketConfiguration;

        BucketLevel(String levelName, BucketConfiguration bucketConfiguration) {
            this.levelName = levelName;
            this.bucketConfiguration = bucketConfiguration;
        }
    }
}
