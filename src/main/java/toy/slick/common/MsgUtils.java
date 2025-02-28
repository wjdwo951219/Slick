package toy.slick.common;

public class MsgUtils {
    private MsgUtils() {
    }

    public static String deleteCntMsg(int deleteCnt) {
        return "deleteCnt : " + deleteCnt;
    }

    public static String deleteCntMsg(int deleteCnt, int correctDeleteCnt) {
        return deleteCntMsg(deleteCnt) + ", deleteCnt != " + correctDeleteCnt;
    }

    public static String insertCntMsg(int insertCnt) {
        return "insertCnt : " + insertCnt;
    }

    public static String insertCntMsg(int insertCnt, int correctInsertCnt) {
        return insertCntMsg(insertCnt) + ", insertCnt != " + correctInsertCnt;
    }

    public static String nullMsg(Object obj) {
        return obj + "is null";
    }

    public static String emptyMsg(Object obj) {
        return obj + "is empty";
    }

    public static String blankMsg(Object obj) {
        return obj + "is blank";
    }
}
