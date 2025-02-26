package toy.slick.common;

import lombok.Getter;

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

    interface ZoneId {
        String NEW_YORK = "America/New_York";
        String SEOUL = "Asia/Seoul";
        String UTC = "UTC";
    }
}
