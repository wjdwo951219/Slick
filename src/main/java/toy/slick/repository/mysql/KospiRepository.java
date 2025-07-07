package toy.slick.repository.mysql;

import lombok.NonNull;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.generated.tables.JKospi;
import org.jooq.generated.tables.pojos.Kospi;
import org.jooq.generated.tables.records.KospiRecord;
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
                        kospi.getDatetime(),
                        kospi.getUrl(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<Kospi> selectRecentOneIn12Hours() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));
        LocalDateTime from = now.minusHours(12);

        SelectConditionStep<Record> query = this.querySelect(
                tKospi,
                tKospi.DATETIME.between(from, now));

        return Optional.ofNullable(query
                .orderBy(tKospi.DATETIME.desc())
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
