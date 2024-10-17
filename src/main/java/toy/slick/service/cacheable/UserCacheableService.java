package toy.slick.service.cacheable;

import org.jooq.generated.tables.pojos.ApiKey;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import toy.slick.config.redis.RedisConfig;
import toy.slick.repository.mariadb.ApiKeyRepository;
import toy.slick.repository.mariadb.UserRepository;
import toy.slick.service.UserService;

@Service
public class UserCacheableService extends UserService {

    public UserCacheableService(UserRepository userRepository, ApiKeyRepository apiKeyRepository) {
        super(userRepository, apiKeyRepository);
    }

    @Override
    @Cacheable(cacheNames = RedisConfig.CacheNames._10min)
    public ApiKey getApiKey(String requestApiKey, String email) {
        return super.getApiKey(requestApiKey, email);
    }
}
