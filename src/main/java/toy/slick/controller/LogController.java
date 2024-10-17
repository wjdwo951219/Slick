package toy.slick.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Response;
import toy.slick.controller.vo.response.DeleteRes;
import toy.slick.controller.vo.response.LogRes;
import toy.slick.interceptor.ApiKeyInterceptor;
import toy.slick.service.LogService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/log")
public class LogController {
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @TimeLogAspect.TimeLog
    @ApiKeyInterceptor.IsAdmin
    @GetMapping("/list")
    public Response<List<LogRes>> getLogList() {
        return new Response<>(logService.getLogList());
    }

    @TimeLogAspect.TimeLog
    @ApiKeyInterceptor.IsAdmin
    @GetMapping("/list/{likeMessage}")
    public Response<List<LogRes>> getLogList(@PathVariable String likeMessage) {
        return new Response<>(logService.getLogList(likeMessage));
    }

    @TimeLogAspect.TimeLog
    @ApiKeyInterceptor.IsAdmin
    @DeleteMapping("/list")
    public Response<DeleteRes> deleteLogList(@RequestBody List<String> idList) {
        return new Response<>(logService.delete(idList));
    }
}
