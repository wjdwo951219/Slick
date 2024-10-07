package toy.slick.feign.investing.vo.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EconomicIndex {
    private String price;
    private String priceChange;
    private String priceChangePercent;
    private String url;
    private String title;
}
