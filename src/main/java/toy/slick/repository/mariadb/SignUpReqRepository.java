package toy.slick.repository.mariadb;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JSignUpReq;
import org.jooq.generated.tables.pojos.SignUpReq;
import org.jooq.generated.tables.records.SignUpReqRecord;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mariadb.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class SignUpReqRepository extends QueryCRUD<SignUpReqRecord> {
    private final JSignUpReq tSignUpReq = JSignUpReq.SIGN_UP_REQ;

    public SignUpReqRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull SignUpReq signUpReq, @NonNull String regId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<SignUpReqRecord> query = this.queryInsert(
                tSignUpReq,
                new SignUpReqRecord(
                        signUpReq.getEmail(),
                        signUpReq.getPassword(),
                        signUpReq.getAuthCode(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<SignUpReq> select(@NonNull String email, @NonNull String authCode) {
        SelectConditionStep<Record> query = this.querySelect(
                tSignUpReq,
                tSignUpReq.EMAIL.equal(email)
                        .and(tSignUpReq.AUTH_CODE.equal(authCode)));

        return Optional.ofNullable(query.fetchOneInto(SignUpReq.class));
    }

    public int selectCnt(@NonNull String email) {
        return this.querySelectCnt(
                tSignUpReq,
                tSignUpReq.EMAIL.equal(email));
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<SignUpReqRecord> query = this.queryDelete(
                tSignUpReq,
                tSignUpReq.REG_DATETIME.lessOrEqual(untilDateTime));

        return query.execute();
    }

    public int delete(@NonNull String email) {
        DeleteConditionStep<SignUpReqRecord> query = this.queryDelete(
                tSignUpReq,
                tSignUpReq.EMAIL.equal(email));

        return query.execute();
    }
}
