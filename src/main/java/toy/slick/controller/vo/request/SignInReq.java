package toy.slick.controller.vo.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@Getter
public class SignInReq {
    @Email
    private String email;

    @Length(min = 8, max = 20)
    private String password;
}
