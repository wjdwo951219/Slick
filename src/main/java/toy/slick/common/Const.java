package toy.slick.common;

public interface Const {
    interface Emoji {
        String FACE_SCREAMING_IN_FEAR = "\uD83D\uDE31"; // 😱
        String FEARFUL_FACE = "\uD83D\uDE28"; // 😨
        String THINKING_FACE = "\uD83E\uDD14"; // 🤔
        String GRINNING_SQUINTING_FACE = "\uD83D\uDE06"; // 😆
        String ZANY_FACE = "\uD83E\uDD2A"; // 🤪
        String FLAG = "\ud83d\udea9"; // 🚩
        String CHECK_MARK = "\u2705"; // ✅
        String DOWN_CHART = "\uD83D\uDCC9"; // 📉
        String UP_CHART = "\uD83D\uDCC8"; // 📈
        String YEN_BANKNOTE = "\uD83D\uDCB4"; // 💴
        String DOLLAR_BANKNOTE = "\uD83D\uDCB5"; // 💵
        String EURO_BANKNOTE = "\uD83D\uDCB6"; // 💶
    }

    interface ZoneId {
        String NEW_YORK = "America/New_York";
        String SEOUL = "Asia/Seoul";
        String UTC = "UTC";
    }

    interface Country {
        String US = "United States";
        String KR = "South Korea";
    }

    interface HttpRequestHeader {
        String ACCEPT_LANGUAGE = "accept-language=ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";
        String USER_AGENT = "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    }
}
