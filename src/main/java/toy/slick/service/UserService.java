package toy.slick.service;

import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.generated.tables.pojos.ApiKey;
import org.jooq.generated.tables.pojos.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.slick.common.Const;
import toy.slick.controller.vo.request.SignInReq;
import toy.slick.controller.vo.request.SignUpReq;
import toy.slick.exception.AlreadyExistsException;
import toy.slick.exception.EmptyException;
import toy.slick.exception.QueryResultCntException;
import toy.slick.repository.mariadb.ApiKeyRepository;
import toy.slick.repository.mariadb.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;

    public UserService(UserRepository userRepository,
                       ApiKeyRepository apiKeyRepository) {
        this.userRepository = userRepository;
        this.apiKeyRepository = apiKeyRepository;
    }

    @Transactional
    public void signUp(SignUpReq signUpReq) {
        if (userRepository.selectCnt(signUpReq.getEmail()) > 0) {
            throw new AlreadyExistsException("Already registered email.");
        }

        int userSaveCnt = userRepository.save(
                signUpReq.getEmail(),
                signUpReq.getPassword(),
                signUpReq.getEmail(),
                signUpReq.getEmail()
        );

        int apiKeyInsertCnt = apiKeyRepository.insert(
                this.generateApiKey(signUpReq.getEmail()),
                signUpReq.getEmail(),
                "Y",
                Const.Role.USER.getName(),
                Const.BucketLevel.BASIC.getLevelName(),
                signUpReq.getEmail(),
                signUpReq.getEmail()
        );

        if (!(userSaveCnt == 1 && apiKeyInsertCnt == 1)) {
            throw new QueryResultCntException("!(userSaveCnt == 1 && apiKeyInsertCnt == 1)");
        }
    }

    public void signIn(HttpSession session, SignInReq signInReq) {
        if (session.getAttribute("email") != null) {
            throw new AlreadyExistsException("Already signed in. retry after sign out");
        }

        Optional<User> user = userRepository.select(signInReq.getEmail(), signInReq.getPassword());

        if (user.isEmpty()) {
            throw new EmptyException("user is Empty");
        }

        Optional<ApiKey> apiKey = apiKeyRepository.select(user.get().getEmail());

        if (apiKey.isEmpty()) {
            throw new EmptyException("apiKey is Empty");
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
}
