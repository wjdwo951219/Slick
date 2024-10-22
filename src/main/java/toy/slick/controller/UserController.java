package toy.slick.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.slick.aspect.TimeLogAspect;
import toy.slick.common.Response;
import toy.slick.controller.vo.request.SignInReq;
import toy.slick.controller.vo.request.SignUpReq;
import toy.slick.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @TimeLogAspect.TimeLog
    @PostMapping("/signUp")
    public Response<String> signUp(@RequestBody @Valid SignUpReq signUpReq) {
        userService.signUp(signUpReq);

        return new Response<>(HttpStatus.OK.name());
    }

    @TimeLogAspect.TimeLog
    @GetMapping("/signUpConfirm/{email}/{authCode}")
    public Response<String> signUpConfirm(@PathVariable String email, @PathVariable String authCode) {
        userService.signUpConfirm(email, authCode);

        return new Response<>(HttpStatus.OK.name());
    }

    @TimeLogAspect.TimeLog
    @PostMapping("/signIn")
    public Response<String> signIn(HttpServletRequest httpServletRequest,
                                   @RequestBody @Valid SignInReq signInReq) {
        userService.signIn(httpServletRequest.getSession(), signInReq);

        return new Response<>(HttpStatus.OK.name());
    }

    @TimeLogAspect.TimeLog
    @DeleteMapping("/signOut")
    public Response<String> signOut(HttpServletRequest httpServletRequest) {
        userService.signOut(httpServletRequest.getSession());

        return new Response<>(HttpStatus.OK.name());
    }
}
