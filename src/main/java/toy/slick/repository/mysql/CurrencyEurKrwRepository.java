package toy.slick.repository.mysql;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JCurrencyEurKrw;
import org.jooq.generated.tables.pojos.CurrencyEurKrw;
import org.jooq.generated.tables.records.CurrencyEurKrwRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mysql.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class CurrencyEurKrwRepository extends QueryCRUD<CurrencyEurKrwRecord> {
    private final JCurrencyEurKrw tCurrencyEurKrw = JCurrencyEurKrw.CURRENCY_EUR_KRW;

    public CurrencyEurKrwRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull CurrencyEurKrw currencyEurKrw, @NonNull String regId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<CurrencyEurKrwRecord> query = this.queryInsert(
                tCurrencyEurKrw,
                new CurrencyEurKrwRecord(
                        currencyEurKrw.getPrice(),
                        currencyEurKrw.getPriceChange(),
                        currencyEurKrw.getPriceChangePercent(),
                        currencyEurKrw.getUrl(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<CurrencyEurKrw> selectRecentOne() {
        SelectConditionStep<Record> query = this.querySelect(
                tCurrencyEurKrw,
                DSL.noCondition());

        return Optional.ofNullable(query
                .orderBy(tCurrencyEurKrw.REG_DATETIME.desc())
                .limit(1)
                .fetchOneInto(CurrencyEurKrw.class));
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<CurrencyEurKrwRecord> query = this.queryDelete(
                tCurrencyEurKrw,
                tCurrencyEurKrw.REG_DATETIME.lessThan(untilDateTime));

        return query.execute();
    }
}
