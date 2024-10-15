package toy.slick.repository.mariadb;

import org.jooq.DSLContext;
import org.jooq.generated.tables.JApiKey;
import org.jooq.generated.tables.pojos.ApiKey;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ApiKeyRepository {
    private final JApiKey tApiKey = JApiKey.API_KEY;
    private final DSLContext dslContext;

    public ApiKeyRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public int insert(String key, String email, String useYn, String role, String bucketLevel, String regId, String uptId) {
        return dslContext.insertInto(tApiKey)
                .set(tApiKey.KEY, key)
                .set(tApiKey.EMAIL, email)
                .set(tApiKey.USE_YN, useYn)
                .set(tApiKey.EXPIRED_DATETIME, DSL.currentLocalDateTime().plus(365))
                .set(tApiKey.ROLE, role)
                .set(tApiKey.BUCKET_LEVEL, bucketLevel)
                .set(tApiKey.REG_ID, regId)
                .set(tApiKey.REG_DATETIME, DSL.currentLocalDateTime())
                .set(tApiKey.UPT_ID, uptId)
                .set(tApiKey.UPT_DATETIME, DSL.currentLocalDateTime())
                .execute();
    }

    public Optional<ApiKey> select(String email) {
        ApiKey apiKey = dslContext.select()
                .from(tApiKey)
                .where(tApiKey.EMAIL.equal(email))
                .fetchOneInto(ApiKey.class);

        return apiKey == null
                ? Optional.empty()
                : Optional.of(apiKey);
    }

    public Optional<ApiKey> select(String requestApiKey, String email) {
        ApiKey apiKey = dslContext.select()
                .from(tApiKey)
                .where(tApiKey.KEY.equal(requestApiKey), tApiKey.EMAIL.equal(email))
                .fetchOneInto(ApiKey.class);

        return apiKey == null
                ? Optional.empty()
                : Optional.of(apiKey);
    }
}
