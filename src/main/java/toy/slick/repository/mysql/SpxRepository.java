package toy.slick.repository.mysql;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JSpx;
import org.jooq.generated.tables.pojos.Spx;
import org.jooq.generated.tables.records.SpxRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mysql.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class SpxRepository extends QueryCRUD<SpxRecord> {
    private final JSpx tSpx = JSpx.SPX;

    public SpxRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull Spx spx, @NonNull String regId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<SpxRecord> query = this.queryInsert(
                tSpx,
                new SpxRecord(
                        spx.getTitle(),
                        spx.getPrice(),
                        spx.getPriceChange(),
                        spx.getPriceChangePercent(),
                        spx.getUrl(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<Spx> selectRecentOne() {
        SelectConditionStep<Record> query = this.querySelect(
                tSpx,
                DSL.noCondition());

        return Optional.ofNullable(query
                .orderBy(tSpx.REG_DATETIME.desc())
                .limit(1)
                .fetchOneInto(Spx.class));
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<SpxRecord> query = this.queryDelete(
                tSpx,
                tSpx.REG_DATETIME.lessThan(untilDateTime));

        return query.execute();
    }
}
