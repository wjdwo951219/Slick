package toy.slick.feign.investing.vo.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class Holiday {
    private LocalDate date;
    private String country;
}
