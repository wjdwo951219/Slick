package toy.slick.repository.mariadb;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JDji;
import org.jooq.generated.tables.pojos.Dji;
import org.jooq.generated.tables.records.DjiRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mariadb.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class DjiRepository extends QueryCRUD<DjiRecord> {
    private final JDji tDji = JDji.DJI;

    public DjiRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull Dji dji, @NonNull String regId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<DjiRecord> query = this.queryInsert(
                tDji,
                new DjiRecord(
                        dji.getTitle(),
                        dji.getPrice(),
                        dji.getPriceChange(),
                        dji.getPriceChangePercent(),
                        dji.getUrl(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<Dji> selectRecentOne() {
        SelectConditionStep<Record> query = this.querySelect(
                tDji,
                DSL.noCondition());

        return Optional.ofNullable(query
                .orderBy(tDji.REG_DATETIME.desc())
                .limit(1)
                .fetchOneInto(Dji.class));
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<DjiRecord> query = this.queryDelete(
                tDji,
                tDji.REG_DATETIME.lessThan(untilDateTime));

        return query.execute();
    }
}
