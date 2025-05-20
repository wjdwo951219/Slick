package toy.slick.repository.mysql;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JCurrencyUsdKrw;
import org.jooq.generated.tables.pojos.CurrencyUsdKrw;
import org.jooq.generated.tables.records.CurrencyUsdKrwRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mysql.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class CurrencyUsdKrwRepository extends QueryCRUD<CurrencyUsdKrwRecord> {
    private final JCurrencyUsdKrw tCurrencyUsdKrw = JCurrencyUsdKrw.CURRENCY_USD_KRW;

    public CurrencyUsdKrwRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull CurrencyUsdKrw currencyUsdKrw, @NonNull String regId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<CurrencyUsdKrwRecord> query = this.queryInsert(
                tCurrencyUsdKrw,
                new CurrencyUsdKrwRecord(
                        currencyUsdKrw.getPrice(),
                        currencyUsdKrw.getPriceChange(),
                        currencyUsdKrw.getPriceChangePercent(),
                        currencyUsdKrw.getUrl(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<CurrencyUsdKrw> selectRecentOne() {
        SelectConditionStep<Record> query = this.querySelect(
                tCurrencyUsdKrw,
                DSL.noCondition());

        return Optional.ofNullable(query
                .orderBy(tCurrencyUsdKrw.REG_DATETIME.desc())
                .limit(1)
                .fetchOneInto(CurrencyUsdKrw.class));
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<CurrencyUsdKrwRecord> query = this.queryDelete(
                tCurrencyUsdKrw,
                tCurrencyUsdKrw.REG_DATETIME.lessThan(untilDateTime));

        return query.execute();
    }
}
