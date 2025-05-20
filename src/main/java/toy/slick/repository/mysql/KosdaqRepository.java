package toy.slick.repository.mysql;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JKosdaq;
import org.jooq.generated.tables.pojos.Kosdaq;
import org.jooq.generated.tables.records.KosdaqRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mysql.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class KosdaqRepository extends QueryCRUD<KosdaqRecord> {
    private final JKosdaq tKosdaq = JKosdaq.KOSDAQ;

    public KosdaqRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull Kosdaq kosdaq, @NonNull String regId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<KosdaqRecord> query = this.queryInsert(
                tKosdaq,
                new KosdaqRecord(
                        kosdaq.getTitle(),
                        kosdaq.getPrice(),
                        kosdaq.getPriceChange(),
                        kosdaq.getPriceChangePercent(),
                        kosdaq.getUrl(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<Kosdaq> selectRecentOne() {
        SelectConditionStep<Record> query = this.querySelect(
                tKosdaq,
                DSL.noCondition());

        return Optional.ofNullable(query
                .orderBy(tKosdaq.REG_DATETIME.desc())
                .limit(1)
                .fetchOneInto(Kosdaq.class));
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<KosdaqRecord> query = this.queryDelete(
                tKosdaq,
                tKosdaq.REG_DATETIME.lessThan(untilDateTime));

        return query.execute();
    }
}
