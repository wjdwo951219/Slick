package toy.slick.repository.mariadb;

import lombok.NonNull;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JCurrencyJpyKrw;
import org.jooq.generated.tables.pojos.CurrencyJpyKrw;
import org.jooq.generated.tables.records.CurrencyJpyKrwRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mariadb.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class CurrencyJpyKrwRepository extends QueryCRUD<CurrencyJpyKrwRecord> {
    private final JCurrencyJpyKrw tCurrencyJpyKrw = JCurrencyJpyKrw.CURRENCY_JPY_KRW;

    public CurrencyJpyKrwRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insert(@NonNull CurrencyJpyKrw currencyJpyKrw, @NonNull String regId) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

        InsertSetMoreStep<CurrencyJpyKrwRecord> query = this.queryInsert(
                tCurrencyJpyKrw,
                new CurrencyJpyKrwRecord(
                        currencyJpyKrw.getPrice(),
                        currencyJpyKrw.getPriceChange(),
                        currencyJpyKrw.getPriceChangePercent(),
                        currencyJpyKrw.getUrl(),
                        now,
                        regId,
                        now,
                        regId));

        return query.execute();
    }

    public Optional<CurrencyJpyKrw> selectRecentOne() {
        SelectConditionStep<Record> query = this.querySelect(
                tCurrencyJpyKrw,
                DSL.noCondition());

        return Optional.ofNullable(query
                .orderBy(tCurrencyJpyKrw.REG_DATETIME.desc())
                .limit(1)
                .fetchOneInto(CurrencyJpyKrw.class));
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<CurrencyJpyKrwRecord> query = this.queryDelete(
                tCurrencyJpyKrw,
                tCurrencyJpyKrw.REG_DATETIME.lessThan(untilDateTime));

        return query.execute();
    }
}
