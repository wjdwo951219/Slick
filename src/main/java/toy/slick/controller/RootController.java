package toy.slick.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.slick.common.Response;

@Hidden
@RestController
@RequestMapping("")
public class RootController {

    @GetMapping("/")
    public Response<HttpStatus> root() {
        return new Response<>(HttpStatus.OK);
    }
}
