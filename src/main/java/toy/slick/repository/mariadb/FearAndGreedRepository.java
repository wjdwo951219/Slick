package toy.slick.repository.mariadb;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JFearAndGreed;
import org.jooq.generated.tables.pojos.FearAndGreed;
import org.jooq.generated.tables.records.FearAndGreedRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mariadb.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class FearAndGreedRepository extends QueryCRUD<FearAndGreedRecord> {
    private final JFearAndGreed tFearAndGreed = JFearAndGreed.FEAR_AND_GREED;

    public FearAndGreedRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull FearAndGreed fearAndGreed) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<FearAndGreedRecord> query = this.queryInsert(
                tFearAndGreed,
                new FearAndGreedRecord(
                        fearAndGreed.getRating(),
                        fearAndGreed.getScore(),
                        now,
                        fearAndGreed.getRegId(),
                        now,
                        fearAndGreed.getUptId()
                )
        );

        return query.execute();

//        return dslContext.insertInto(tFearAndGreed)
//                .set(tFearAndGreed.RATING, rating)
//                .set(tFearAndGreed.SCORE, score)
//                .set(tFearAndGreed.REG_ID, regId)
//                .set(tFearAndGreed.REG_DATETIME, DSL.currentLocalDateTime())
//                .set(tFearAndGreed.UPT_ID, uptId)
//                .set(tFearAndGreed.UPT_DATETIME, DSL.currentLocalDateTime())
//                .execute();
    }

    public Optional<FearAndGreed> selectRecentOne() {
        SelectConditionStep<Record> query = this.querySelect(
                tFearAndGreed,
                DSL.noCondition()
        );

        return Optional.ofNullable(query
                .orderBy(tFearAndGreed.REG_DATETIME.desc())
                .limit(1)
                .fetchOneInto(FearAndGreed.class));

//        FearAndGreed queryResult = dslContext.select()
//                .from(tFearAndGreed)
//                .orderBy(tFearAndGreed.REG_DATETIME.desc())
//                .limit(1)
//                .fetchOneInto(FearAndGreed.class);
//
//        return queryResult == null ? Optional.empty() : Optional.of(queryResult);
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<FearAndGreedRecord> query = this.queryDelete(
                tFearAndGreed,
                tFearAndGreed.REG_DATETIME.lessThan(untilDateTime)
        );

        return query.execute();

//        return dslContext.delete(tFearAndGreed)
//                .where(tFearAndGreed.REG_DATETIME.lessThan(untilDateTime))
//                .execute();
    }
}
