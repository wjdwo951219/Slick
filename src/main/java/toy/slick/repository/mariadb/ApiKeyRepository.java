package toy.slick.repository.mariadb;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JApiKey;
import org.jooq.generated.tables.pojos.ApiKey;
import org.jooq.generated.tables.records.ApiKeyRecord;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mariadb.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class ApiKeyRepository extends QueryCRUD<ApiKeyRecord> {
    private final JApiKey tApiKey = JApiKey.API_KEY;

    public ApiKeyRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull ApiKey apiKey) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<ApiKeyRecord> query = this.queryInsert(
                tApiKey,
                new ApiKeyRecord(
                        apiKey.getKey(),
                        apiKey.getEmail(),
                        apiKey.getUseYn(),
                        now.plusYears(1),
                        apiKey.getRole(),
                        apiKey.getBucketLevel(),
                        now,
                        apiKey.getRegId(),
                        now,
                        apiKey.getUptId()
                )
        );

        return query.execute();

//        return dslContext.insertInto(tApiKey)
//                .set(tApiKey.KEY, key)
//                .set(tApiKey.EMAIL, email)
//                .set(tApiKey.USE_YN, useYn)
//                .set(tApiKey.EXPIRED_DATETIME, DSL.currentLocalDateTime().plus(365))
//                .set(tApiKey.ROLE, role)
//                .set(tApiKey.BUCKET_LEVEL, bucketLevel)
//                .set(tApiKey.REG_ID, regId)
//                .set(tApiKey.REG_DATETIME, DSL.currentLocalDateTime())
//                .set(tApiKey.UPT_ID, uptId)
//                .set(tApiKey.UPT_DATETIME, DSL.currentLocalDateTime())
//                .execute();
    }

    public Optional<ApiKey> select(@NonNull String email) {
        SelectConditionStep<Record> query = this.querySelect(
                tApiKey,
                tApiKey.EMAIL.equal(email)
        );

        return Optional.ofNullable(query.fetchOneInto(ApiKey.class));

//        ApiKey apiKey = dslContext.select()
//                .from(tApiKey)
//                .where(tApiKey.EMAIL.equal(email))
//                .fetchOneInto(ApiKey.class);
//
//        return apiKey == null
//                ? Optional.empty()
//                : Optional.of(apiKey);
    }

    public Optional<ApiKey> select(@NonNull String requestApiKey, @NonNull String email) {
        SelectConditionStep<Record> query = this.querySelect(
                tApiKey,
                tApiKey.KEY.equal(requestApiKey)
                        .and(tApiKey.EMAIL.equal(email))
        );

        return Optional.ofNullable(query.fetchOneInto(ApiKey.class));

//        ApiKey apiKey = dslContext.select()
//                .from(tApiKey)
//                .where(tApiKey.KEY.equal(requestApiKey), tApiKey.EMAIL.equal(email))
//                .fetchOneInto(ApiKey.class);
//
//        return apiKey == null
//                ? Optional.empty()
//                : Optional.of(apiKey);
    }
}
