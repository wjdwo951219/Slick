package toy.slick.config.redis;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import toy.slick.common.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig implements CachingConfigurer {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    public interface CacheNames {
        String prefix = "Cache";
        String _10sec = prefix + "_10sec";
        String _30sec = prefix + "_30sec";
        String _1min = prefix + "_1min";
        String _10min = prefix + "_10min";
        String _20min = prefix + "_20min";
        String _30min = prefix + "_30min";
        String _1hour = prefix + "_1hour";
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();

        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setUsername(username);
        redisStandaloneConfiguration.setPassword(password);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(ObjectMapper.getInstance()));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(ObjectMapper.getInstance()));
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(ObjectMapper.getInstance())));

        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>() {{
            put(CacheNames._10sec, redisCacheConfiguration.entryTtl(Duration.ofSeconds(10)));
            put(CacheNames._30sec, redisCacheConfiguration.entryTtl(Duration.ofSeconds(30)));
            put(CacheNames._1min, redisCacheConfiguration.entryTtl(Duration.ofMinutes(1)));
            put(CacheNames._10min, redisCacheConfiguration.entryTtl(Duration.ofMinutes(10)));
            put(CacheNames._20min, redisCacheConfiguration.entryTtl(Duration.ofMinutes(20)));
            put(CacheNames._30min, redisCacheConfiguration.entryTtl(Duration.ofMinutes(30)));
            put(CacheNames._1hour, redisCacheConfiguration.entryTtl(Duration.ofHours(1)));
        }};

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(redisCacheConfigurationMap)
                .build();
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> method.getDeclaringClass().getSimpleName()
                + "."
                + method.getName()
                + "("
                + StringUtils.join(params, ", ")
                + ")";
    }

    @Bean
    public LettuceBasedProxyManager<String> lettuceBasedProxyManager() {
        StatefulRedisConnection<String, byte[]> statefulRedisConnection;

        try (RedisClient redisClient = RedisClient.create(RedisURI.builder()
                .withHost(this.host)
                .withPort(this.port)
                .withClientName(this.username)
                .withPassword(this.password.toCharArray())
                .build())) {
            statefulRedisConnection = redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
        }

        ExpirationAfterWriteStrategy expirationAfterWriteStrategy
                = ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(30));

        return LettuceBasedProxyManager
                .builderFor(statefulRedisConnection)
                .withClientSideConfig(ClientSideConfig.getDefault()
                        .withExpirationAfterWriteStrategy(expirationAfterWriteStrategy))
                .build();
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(ObjectMapper.getInstance());
    }
}
