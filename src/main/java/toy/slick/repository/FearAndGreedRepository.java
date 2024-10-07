package toy.slick.repository;

import org.jooq.DSLContext;
import org.jooq.generated.tables.JFearAndGreed;
import org.jooq.generated.tables.pojos.FearAndGreed;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class FearAndGreedRepository {
    private final JFearAndGreed tFearAndGreed = JFearAndGreed.FEAR_AND_GREED;
    private final DSLContext dslContext;

    public FearAndGreedRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void insert(String rating, double score, String regId, String uptId) {
        dslContext.insertInto(tFearAndGreed)
                .set(tFearAndGreed.RATING, rating)
                .set(tFearAndGreed.SCORE, score)
                .set(tFearAndGreed.REG_ID, regId)
                .set(tFearAndGreed.REG_DATETIME, DSL.currentLocalDateTime())
                .set(tFearAndGreed.UPT_ID, uptId)
                .set(tFearAndGreed.UPT_DATETIME, DSL.currentLocalDateTime())
                .execute();
    }

    public Optional<FearAndGreed> selectRecentOne() {
        FearAndGreed queryResult = dslContext.select()
                .from(tFearAndGreed)
                .orderBy(tFearAndGreed.REG_DATETIME.desc())
                .limit(1)
                .fetchOneInto(FearAndGreed.class);

        return queryResult == null ? Optional.empty() : Optional.of(queryResult);
    }

    public int delete(LocalDateTime untilDateTime) {
        return dslContext.delete(tFearAndGreed)
                .where(tFearAndGreed.REG_DATETIME.lessThan(untilDateTime))
                .execute();
    }
}
