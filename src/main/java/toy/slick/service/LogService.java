package toy.slick.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import toy.slick.controller.vo.response.DeleteRes;
import toy.slick.controller.vo.response.LogRes;
import toy.slick.repository.mongodb.LogRepository;
import toy.slick.repository.mongodb.MongoData;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {
    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public LogRes getLogList() {
        List<LogRepository.Log> logList = logRepository.findAll(Sort.by("_id").descending());

        return LogRes.builder()
                .idList(logList
                        .stream()
                        .map(MongoData::get_id)
                        .collect(Collectors.toList()))
                .logList(logList
                        .stream()
                        .map(o -> LogRes.Log.builder()
                                .id(o.get_id())
                                .message(o.getLog())
                                .timestamp(o.get_timestamp())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public LogRes getLogList(String likeMessage) {
        List<LogRepository.Log> logList = logRepository.findAll(Sort.by("_id").descending())
                .stream()
                .filter(o -> o.getLog().contains(likeMessage))
                .toList();

        return LogRes.builder()
                .idList(logList
                        .stream()
                        .map(MongoData::get_id)
                        .collect(Collectors.toList()))
                .logList(logList
                        .stream()
                        .map(o -> LogRes.Log.builder()
                                .id(o.get_id())
                                .message(o.getLog())
                                .timestamp(o.get_timestamp())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public DeleteRes delete(List<String> idList) {
        long deleteCnt = logRepository.deleteAllBy_idIn(idList);

        return DeleteRes.builder()
                .deleteCnt(deleteCnt)
                .build();
    }
}
