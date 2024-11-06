package toy.slick.repository.redis;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;
import toy.slick.config.redis.RedisConfig;

@Repository
public class CacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public CacheRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long delete() {
        long cnt = 0;

        try (Cursor<String> redisKeyCursor = redisTemplate.scan(ScanOptions.scanOptions()
                .match(RedisConfig.CacheNames.prefix + "*")
                .build())) {
            while (redisKeyCursor.hasNext()) {
                redisTemplate.unlink(redisKeyCursor.next());
                cnt++;
            }
        }

        return cnt;
    }

    public long delete(String cacheName) {
        long cnt = 0;

        try (Cursor<String> redisKeyCursor = redisTemplate.scan(ScanOptions.scanOptions()
                .match(RedisConfig.CacheNames.prefix + cacheName + "::*")
                .build())) {
            while (redisKeyCursor.hasNext()) {
                redisTemplate.unlink(redisKeyCursor.next());
                cnt++;
            }
        }

        return cnt;
    }

    public long delete(String cacheName, String objectName) {
        long cnt = 0;

        try (Cursor<String> redisKeyCursor = redisTemplate.scan(ScanOptions.scanOptions()
                .match(RedisConfig.CacheNames.prefix + cacheName + "::" + objectName + "\\.*")
                .build())) {
            while (redisKeyCursor.hasNext()) {
                redisTemplate.unlink(redisKeyCursor.next());
                cnt++;
            }
        }

        return cnt;
    }

    public long delete(String cacheName, String objectName, String methodName) {
        long cnt = 0;

        try (Cursor<String> redisKeyCursor = redisTemplate.scan(ScanOptions.scanOptions()
                .match(RedisConfig.CacheNames.prefix + cacheName + "::" + objectName + "\\." + methodName + "\\(*")
                .build())) {
            while (redisKeyCursor.hasNext()) {
                redisTemplate.unlink(redisKeyCursor.next());
                cnt++;
            }
        }

        return cnt;
    }
}
