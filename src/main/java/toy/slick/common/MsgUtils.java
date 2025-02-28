package toy.slick.common;

public class MsgUtils {
    private MsgUtils() {
    }

    public static String deleteCntMsg(int deleteCnt) {
        return "deleteCnt : " + deleteCnt;
    }

    public static String insertCntMsg(int insertCnt) {
        return "insertCnt : " + insertCnt;
    }

    public static String emptyMsg(Object obj) {
        return obj + "is Empty";
    }

    public static String blankMsg(Object obj) {
        return obj + "is Blank";
    }
}
