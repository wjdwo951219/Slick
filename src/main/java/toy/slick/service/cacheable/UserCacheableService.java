package toy.slick.service.cacheable;

import org.jooq.generated.tables.pojos.ApiKey;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import toy.slick.config.redis.RedisConfig;
import toy.slick.repository.mariadb.ApiKeyRepository;
import toy.slick.repository.mariadb.SignUpReqRepository;
import toy.slick.repository.mariadb.UserRepository;
import toy.slick.service.UserService;

@Service
public class UserCacheableService extends UserService {

    public UserCacheableService(RedisTemplate<String, Object> redisTemplate,
                                JavaMailSender javaMailSender,
                                UserRepository userRepository,
                                ApiKeyRepository apiKeyRepository,
                                SignUpReqRepository signUpReqRepository) {
        super(redisTemplate, javaMailSender, userRepository, apiKeyRepository, signUpReqRepository);
    }

    @Override
    @Cacheable(cacheNames = RedisConfig.CacheNames._10min)
    public ApiKey getApiKey(String requestApiKey, String email) {
        return super.getApiKey(requestApiKey, email);
    }
}
