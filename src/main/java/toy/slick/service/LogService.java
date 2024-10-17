package toy.slick.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import toy.slick.controller.vo.response.DeleteRes;
import toy.slick.controller.vo.response.LogRes;
import toy.slick.repository.mongodb.LogRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public List<LogRes> getLogList() {
        return logRepository.findAll(Sort.by("_id").descending())
                .stream()
                .map(o -> LogRes.builder()
                        .id(o.get_id())
                        .message(o.getLog())
                        .timestamp(o.get_timestamp())
                        .build())
                .collect(Collectors.toList());
    }

    public List<LogRes> getLogList(String likeMessage) {
        return logRepository.findAll(Sort.by("_id").descending())
                .stream()
                .filter(o -> o.getLog().contains(likeMessage))
                .map(o -> LogRes.builder()
                        .id(o.get_id())
                        .message(o.getLog())
                        .timestamp(o.get_timestamp())
                        .build())
                .collect(Collectors.toList());
    }

    public DeleteRes delete(List<String> idList) {
        long deleteCnt = logRepository.deleteAllBy_idIn(idList);

        return DeleteRes.builder()
                .deleteCnt(deleteCnt)
                .build();
    }
}
