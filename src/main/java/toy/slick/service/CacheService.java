package toy.slick.service;

import org.springframework.stereotype.Service;
import toy.slick.controller.vo.response.DeleteCntRes;
import toy.slick.repository.redis.CacheRepository;

@Service
public class CacheService {
    private final CacheRepository cacheRepository;

    public CacheService(CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    public DeleteCntRes delete() {
        return DeleteCntRes.builder()
                .deleteCnt(cacheRepository.delete())
                .build();
    }

    public DeleteCntRes delete(String cacheName) {
        return DeleteCntRes.builder()
                .deleteCnt(cacheRepository.delete(cacheName))
                .build();
    }

    public DeleteCntRes delete(String cacheName, String objectName) {
        return DeleteCntRes.builder()
                .deleteCnt(cacheRepository.delete(cacheName, objectName))
                .build();
    }

    public DeleteCntRes delete(String cacheName, String objectName, String methodName) {
        return DeleteCntRes.builder()
                .deleteCnt(cacheRepository.delete(cacheName, objectName, methodName))
                .build();
    }
}
