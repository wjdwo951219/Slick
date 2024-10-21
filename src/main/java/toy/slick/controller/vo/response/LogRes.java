package toy.slick.controller.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogRes {
    private List<String> idList;
    private List<Log> logList;

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Log {
        private String id;
        private String message;
        private Date timestamp;
    }
}
