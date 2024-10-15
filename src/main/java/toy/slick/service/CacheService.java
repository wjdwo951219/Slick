package toy.slick.service;

import org.springframework.stereotype.Service;
import toy.slick.controller.vo.response.DeleteRes;
import toy.slick.repository.redis.CacheRepository;

@Service
public class CacheService {
    private final CacheRepository cacheRepository;

    public CacheService(CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    public DeleteRes delete() {
        return DeleteRes.builder()
                .deleteCnt(cacheRepository.delete())
                .build();
    }

    public DeleteRes delete(String cacheName) {
        return DeleteRes.builder()
                .deleteCnt(cacheRepository.delete(cacheName))
                .build();
    }

    public DeleteRes delete(String cacheName, String objectName) {
        return DeleteRes.builder()
                .deleteCnt(cacheRepository.delete(cacheName, objectName))
                .build();
    }

    public DeleteRes delete(String cacheName, String objectName, String methodName) {
        return DeleteRes.builder()
                .deleteCnt(cacheRepository.delete(cacheName, objectName, methodName))
                .build();
    }
}
