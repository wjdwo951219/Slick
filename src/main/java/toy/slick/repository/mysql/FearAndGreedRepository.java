package toy.slick.repository.mysql;

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
import toy.slick.repository.mysql.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class FearAndGreedRepository extends QueryCRUD<FearAndGreedRecord> {
    private final JFearAndGreed tFearAndGreed = JFearAndGreed.FEAR_AND_GREED;

    public FearAndGreedRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull FearAndGreed fearAndGreed, @NonNull String regId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<FearAndGreedRecord> query = this.queryInsert(
                tFearAndGreed,
                new FearAndGreedRecord(
                        fearAndGreed.getRating(),
                        fearAndGreed.getScore(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<FearAndGreed> selectRecentOne() {
        SelectConditionStep<Record> query = this.querySelect(
                tFearAndGreed,
                DSL.noCondition());

        return Optional.ofNullable(query
                .orderBy(tFearAndGreed.REG_DATETIME.desc())
                .limit(1)
                .fetchOneInto(FearAndGreed.class));
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<FearAndGreedRecord> query = this.queryDelete(
                tFearAndGreed,
                tFearAndGreed.REG_DATETIME.lessThan(untilDateTime));

        return query.execute();
    }
}
