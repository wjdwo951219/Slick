package toy.slick.service;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.generated.tables.pojos.ApiKey;
import org.jooq.generated.tables.pojos.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.slick.common.Const;
import toy.slick.controller.vo.request.SignInReq;
import toy.slick.controller.vo.request.SignUpReq;
import toy.slick.exception.AlreadyExistsException;
import toy.slick.exception.EmptyException;
import toy.slick.exception.QueryResultCntException;
import toy.slick.repository.mariadb.ApiKeyRepository;
import toy.slick.repository.mariadb.SignUpReqRepository;
import toy.slick.repository.mariadb.UserRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final SignUpReqRepository signUpReqRepository;

    public UserService(JavaMailSender javaMailSender,
                       UserRepository userRepository,
                       ApiKeyRepository apiKeyRepository,
                       SignUpReqRepository signUpReqRepository) {
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.signUpReqRepository = signUpReqRepository;
    }

    @Transactional
    public void signUpConfirm(String email, String authCode) {
        Optional<org.jooq.generated.tables.pojos.SignUpReq> signUpReq = signUpReqRepository.select(email, authCode);

        if (signUpReq.isEmpty()) {
            throw new EmptyException("not correct data.");
        }

        if (userRepository.selectCnt(email) > 0) {
            throw new AlreadyExistsException("Already verified email");
        }

        int userInsertCnt = userRepository.insert(
                User.builder()
                        .email(signUpReq.get().getEmail())
                        .password(signUpReq.get().getPassword())
                        .build(),
                signUpReq.get().getEmail());

        if (userInsertCnt != 1) {
            throw new QueryResultCntException("userInsertCnt != 1");
        }

        int apiKeyInsertCnt = apiKeyRepository.insert(
                ApiKey.builder()
                        .key(this.generateApiKey(signUpReq.get().getEmail()))
                        .email(signUpReq.get().getEmail())
                        .useYn("Y")
                        .role(Const.Role.USER.getName())
                        .bucketLevel(Const.BucketLevel.BASIC.getLevelName())
                        .build(),
                signUpReq.get().getEmail());

        if (apiKeyInsertCnt != 1) {
            throw new QueryResultCntException("apiKeyInsertCnt != 1");
        }

        int SignUpReqDeleteCnt = signUpReqRepository.delete(signUpReq.get().getEmail());

        if (SignUpReqDeleteCnt != 1) {
            throw new QueryResultCntException("SignUpReqDeleteCnt != 1");
        }
    }

    @Transactional
    public void signUp(SignUpReq signUpReq) {
        if (signUpReqRepository.selectCnt(signUpReq.getEmail()) > 0 || userRepository.selectCnt(signUpReq.getEmail()) > 0) {
            throw new AlreadyExistsException("Already registered email.");
        }

        String authCode = this.generateAuthCode();

        int signUpReqInsertCnt = signUpReqRepository.insert(
                org.jooq.generated.tables.pojos.SignUpReq.builder()
                        .email(signUpReq.getEmail())
                        .password(DigestUtils.sha512Hex(signUpReq.getPassword()))
                        .authCode(authCode)
                        .build(),
                signUpReq.getEmail());

        if (signUpReqInsertCnt != 1) {
            throw new QueryResultCntException("signUpReqInsertCnt != 1");
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(signUpReq.getEmail());
        mailMessage.setText("https://port-0-slick-lxshnvm735fbe3a8.sel5.cloudtype.app/api/user/signUpConfirm/" + signUpReq.getEmail() + "/" + authCode);
        mailMessage.setSubject("[Slick] Verify your email within 5 minutes");

        javaMailSender.send(mailMessage);
    }

    @Transactional
    public void signIn(HttpSession session, SignInReq signInReq) {
        if (session.getAttribute("email") != null) {
            throw new AlreadyExistsException("Already signed in. retry after sign out");
        }

        Optional<User> user = userRepository.select(signInReq.getEmail(), DigestUtils.sha512Hex(signInReq.getPassword()));

        if (user.isEmpty()) {
            throw new EmptyException("user is Empty");
        }

        Optional<ApiKey> apiKey = apiKeyRepository.select(user.get().getEmail());

        if (apiKey.isEmpty()) {
            throw new EmptyException("apiKey is Empty");
        }

        int updateCnt = apiKeyRepository.updateExpiredDateTime(apiKey.get().getKey(), user.get().getEmail());

        if (updateCnt != 1) {
            throw new QueryResultCntException("updateCnt != 1");
        }

        session.setAttribute("email", user.get().getEmail());
        session.setAttribute("requestApiKey", apiKey.get().getKey());
    }

    public void signOut(HttpSession session) {
        session.invalidate();
    }

    public ApiKey getApiKey(String requestApiKey, String email) {
        Optional<ApiKey> apiKey = apiKeyRepository.select(requestApiKey, email);

        if (apiKey.isEmpty()) {
            throw new EmptyException("apiKey is Empty");
        }

        return apiKey.get();
    }

    private String generateApiKey(String str) {
        List<Character> chList = new ArrayList<>();

        for (char ch : str.concat(String.valueOf(System.currentTimeMillis())).toCharArray()) {
            chList.add(ch);
        }

        Collections.shuffle(chList);

        return DigestUtils.sha3_512Hex(StringUtils.join(chList));
    }

    private String generateAuthCode() {
        long randomNo = (long) (new SecureRandom().nextDouble() * (long) Math.pow(10, 12))
                + System.currentTimeMillis()
                + System.nanoTime();

        return DigestUtils.sha512Hex(String.valueOf(randomNo));
    }
}
