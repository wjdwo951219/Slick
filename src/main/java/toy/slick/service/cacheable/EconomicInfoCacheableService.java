package toy.slick.service.cacheable;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import toy.slick.config.redis.RedisConfig;
import toy.slick.controller.vo.response.FearAndGreedRes;
import toy.slick.repository.mariadb.FearAndGreedRepository;
import toy.slick.service.EconomicInfoService;

@Service
public class EconomicInfoCacheableService extends EconomicInfoService {

    public EconomicInfoCacheableService(FearAndGreedRepository fearAndGreedRepository) {
        super(fearAndGreedRepository);
    }

    @Override
    @Cacheable(cacheNames = RedisConfig.CacheNames._10min)
    public FearAndGreedRes getRecentFearAndGreed() {
        return super.getRecentFearAndGreed();
    }
}
