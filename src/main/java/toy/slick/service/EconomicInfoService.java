package toy.slick.service;

import org.jooq.generated.tables.pojos.FearAndGreed;
import org.springframework.stereotype.Service;
import toy.slick.controller.vo.response.FearAndGreedRes;
import toy.slick.exception.EmptyException;
import toy.slick.repository.mariadb.FearAndGreedRepository;

import java.util.Optional;

@Service
public class EconomicInfoService {
    private final FearAndGreedRepository fearAndGreedRepository;

    public EconomicInfoService(FearAndGreedRepository fearAndGreedRepository) {
        this.fearAndGreedRepository = fearAndGreedRepository;
    }

    public FearAndGreedRes getRecentFearAndGreed() {
        Optional<FearAndGreed> fearAndGreed = fearAndGreedRepository.selectRecentOne();

        if (fearAndGreed.isEmpty()) {
            throw new EmptyException("fearAndGreed is Empty");
        }

        return FearAndGreedRes.builder()
                .score(fearAndGreed.get().getScore())
                .rating(fearAndGreed.get().getRating())
                .build();
    }
}
