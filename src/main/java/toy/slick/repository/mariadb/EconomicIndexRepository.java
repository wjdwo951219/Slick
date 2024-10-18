package toy.slick.repository.mariadb;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JEconomicIndex;
import org.jooq.generated.tables.pojos.EconomicIndex;
import org.jooq.generated.tables.records.EconomicIndexRecord;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mariadb.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class EconomicIndexRepository extends QueryCRUD<EconomicIndexRecord> {
    private final JEconomicIndex tEconomicIndex = JEconomicIndex.ECONOMIC_INDEX;

    public EconomicIndexRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull EconomicIndex economicIndex, @NonNull String regId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<EconomicIndexRecord> query = this.queryInsert(
                tEconomicIndex,
                new EconomicIndexRecord(
                        economicIndex.getCode(),
                        economicIndex.getTitle(),
                        economicIndex.getPrice(),
                        economicIndex.getPriceChange(),
                        economicIndex.getPriceChangePercent(),
                        economicIndex.getUrl(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<EconomicIndex> select(@NonNull String code) {
        SelectConditionStep<Record> query = this.querySelect(
                tEconomicIndex,
                tEconomicIndex.CODE.equal(code));

        return Optional.ofNullable(query.fetchOneInto(EconomicIndex.class));
    }

    public int delete(@NonNull String code) {
        DeleteConditionStep<EconomicIndexRecord> query = this.queryDelete(
                tEconomicIndex,
                tEconomicIndex.CODE.equal(code));

        return query.execute();
    }
}
