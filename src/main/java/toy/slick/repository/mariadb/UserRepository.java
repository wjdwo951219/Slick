package toy.slick.repository.mariadb;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JUser;
import org.jooq.generated.tables.pojos.User;
import org.jooq.generated.tables.records.UserRecord;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mariadb.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class UserRepository extends QueryCRUD<UserRecord> {
    private final JUser tUser = JUser.USER;

    public UserRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull User user) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<UserRecord> query = this.queryInsert(
                tUser,
                new UserRecord(
                        user.getEmail(),
                        user.getPassword(),
                        now,
                        user.getRegId(),
                        now,
                        user.getUptId()
                )
        );

        return query.execute();

//        dslContext.insertInto(tUser)
//                .set(tUser.EMAIL, email)
//                .set(tUser.PASSWORD, DigestUtils.sha512Hex(password))
//                .set(tUser.REG_ID, regId)
//                .set(tUser.REG_DATETIME, DSL.currentLocalDateTime())
//                .set(tUser.UPT_ID, uptId)
//                .set(tUser.UPT_DATETIME, DSL.currentLocalDateTime())
//                .onDuplicateKeyUpdate()
//                .set(tUser.PASSWORD, DigestUtils.sha512Hex(password))
//                .set(tUser.UPT_ID, uptId)
//                .set(tUser.UPT_DATETIME, DSL.currentLocalDateTime())
//                .execute();
    }

    public Optional<User> select(@NonNull String email, @NonNull String password) {
        SelectConditionStep<Record> query = this.querySelect(
                tUser,
                tUser.EMAIL.equal(email)
                        .and(tUser.PASSWORD.equal(password))
        );

        return Optional.ofNullable(query.fetchOneInto(User.class));

//        User user = dslContext.select()
//                .from(tUser)
//                .where(tUser.EMAIL.equal(email),
//                        tUser.PASSWORD.equal(DigestUtils.sha512Hex(password)))
//                .fetchOneInto(User.class);
//
//        return user == null
//                ? Optional.empty()
//                : Optional.of(user);
    }

    public int selectCnt(@NonNull String email) {
        return this.querySelectCnt(
                tUser,
                tUser.EMAIL.equal(email)
        );
    }
}
