package toy.slick.repository.mariadb;

import org.apache.commons.codec.digest.DigestUtils;
import org.jooq.DSLContext;
import org.jooq.generated.tables.JUser;
import org.jooq.generated.tables.pojos.User;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
    private final JUser tUser = JUser.USER;
    private final DSLContext dslContext;

    public UserRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void save(String email, String password, String regId, String uptId) {
        dslContext.insertInto(tUser)
                .set(tUser.EMAIL, email)
                .set(tUser.PASSWORD, DigestUtils.sha512Hex(password))
                .set(tUser.REG_ID, regId)
                .set(tUser.REG_DATETIME, DSL.currentLocalDateTime())
                .set(tUser.UPT_ID, uptId)
                .set(tUser.UPT_DATETIME, DSL.currentLocalDateTime())
                .onDuplicateKeyUpdate()
                .set(tUser.PASSWORD, DigestUtils.sha512Hex(password))
                .set(tUser.UPT_ID, uptId)
                .set(tUser.UPT_DATETIME, DSL.currentLocalDateTime())
                .execute();
    }

    public Optional<User> select(String email, String password) {
        User user = dslContext.select()
                .from(tUser)
                .where(tUser.EMAIL.equal(email),
                        tUser.PASSWORD.equal(DigestUtils.sha512Hex(password)))
                .fetchOneInto(User.class);

        return user == null
                ? Optional.empty()
                : Optional.of(user);
    }

    public int selectCnt(String email) {
        return dslContext.selectCount()
                .from(tUser)
                .where(tUser.EMAIL.equal(email))
                .fetch()
                .get(0)
                .value1();
    }
}
