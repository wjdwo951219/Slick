package toy.slick.feign.economicCalendar.vo.response;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
public class EconomicEvent {
    private ZonedDateTime zonedDateTime;
    private String id;
    private String name;
    private String country;
    private String importance;
    private String actual;
    private String forecast;
    private String previous;
}
