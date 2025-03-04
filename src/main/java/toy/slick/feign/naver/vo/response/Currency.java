package toy.slick.feign.naver.vo.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Currency {
    private String price;
    private String priceChange;
    private String priceChangePercent;
    private String url;
}
