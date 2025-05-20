package toy.slick.repository.mysql;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JKospi;
import org.jooq.generated.tables.pojos.Kospi;
import org.jooq.generated.tables.records.KospiRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mysql.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class KospiRepository extends QueryCRUD<KospiRecord> {
    private final JKospi tKospi = JKospi.KOSPI;

    public KospiRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull Kospi kospi, @NonNull String regId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<KospiRecord> query = this.queryInsert(
                tKospi,
                new KospiRecord(
                        kospi.getTitle(),
                        kospi.getPrice(),
                        kospi.getPriceChange(),
                        kospi.getPriceChangePercent(),
                        kospi.getUrl(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<Kospi> selectRecentOne() {
        SelectConditionStep<Record> query = this.querySelect(
                tKospi,
                DSL.noCondition());

        return Optional.ofNullable(query
                .orderBy(tKospi.REG_DATETIME.desc())
                .limit(1)
                .fetchOneInto(Kospi.class));
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<KospiRecord> query = this.queryDelete(
                tKospi,
                tKospi.REG_DATETIME.lessThan(untilDateTime));

        return query.execute();
    }
}
