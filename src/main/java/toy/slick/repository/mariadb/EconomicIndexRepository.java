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

    public int insert(@NonNull EconomicIndex economicIndex) {
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
                        economicIndex.getRegId(),
                        now,
                        economicIndex.getUptId())
        );

        return query.execute();


//        dslContext.insertInto(tEconomicIndex)
//                .set(tEconomicIndex.CODE, code)
//                .set(tEconomicIndex.URL, url)
//                .set(tEconomicIndex.TITLE, title)
//                .set(tEconomicIndex.PRICE, price)
//                .set(tEconomicIndex.PRICE_CHANGE, priceChange)
//                .set(tEconomicIndex.PRICE_CHANGE_PERCENT, priceChangePercent)
//                .set(tEconomicIndex.REG_ID, regId)
//                .set(tEconomicIndex.REG_DATETIME, DSL.currentLocalDateTime())
//                .set(tEconomicIndex.UPT_ID, uptId)
//                .set(tEconomicIndex.UPT_DATETIME, DSL.currentLocalDateTime())
//                .onDuplicateKeyUpdate()
//                .set(tEconomicIndex.URL, url)
//                .set(tEconomicIndex.TITLE, title)
//                .set(tEconomicIndex.PRICE, price)
//                .set(tEconomicIndex.PRICE_CHANGE, priceChange)
//                .set(tEconomicIndex.PRICE_CHANGE_PERCENT, priceChangePercent)
//                .set(tEconomicIndex.UPT_ID, uptId)
//                .set(tEconomicIndex.UPT_DATETIME, DSL.currentLocalDateTime())
//                .execute();
    }

    public Optional<EconomicIndex> select(@NonNull String code) {
        SelectConditionStep<Record> query = this.querySelect(
                tEconomicIndex,
                tEconomicIndex.CODE.equal(code)
        );

        return Optional.ofNullable(query.fetchOneInto(EconomicIndex.class));

//        EconomicIndex queryResult = dslContext.select()
//                .from(tEconomicIndex)
//                .where(tEconomicIndex.CODE.equal(code))
//                .fetchOneInto(EconomicIndex.class);
//
//        return queryResult == null ? Optional.empty() : Optional.of(queryResult);
    }

    public int delete(@NonNull String code) {
        DeleteConditionStep<EconomicIndexRecord> query = this.queryDelete(
                tEconomicIndex,
                tEconomicIndex.CODE.equal(code)
        );

        return query.execute();
    }
}
