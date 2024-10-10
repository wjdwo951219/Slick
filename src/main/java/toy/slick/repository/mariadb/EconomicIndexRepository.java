package toy.slick.repository.mariadb;

import org.jooq.DSLContext;
import org.jooq.generated.tables.JEconomicIndex;
import org.jooq.generated.tables.pojos.EconomicIndex;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EconomicIndexRepository {
    private final JEconomicIndex tEconomicIndex = JEconomicIndex.ECONOMIC_INDEX;
    private final DSLContext dslContext;

    public EconomicIndexRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public void save(String code, String url, String title, String price, String priceChange, String priceChangePercent, String regId, String uptId) {
        dslContext.insertInto(tEconomicIndex)
                .set(tEconomicIndex.CODE, code)
                .set(tEconomicIndex.URL, url)
                .set(tEconomicIndex.TITLE, title)
                .set(tEconomicIndex.PRICE, price)
                .set(tEconomicIndex.PRICE_CHANGE, priceChange)
                .set(tEconomicIndex.PRICE_CHANGE_PERCENT, priceChangePercent)
                .set(tEconomicIndex.REG_ID, regId)
                .set(tEconomicIndex.REG_DATETIME, DSL.currentLocalDateTime())
                .set(tEconomicIndex.UPT_ID, uptId)
                .set(tEconomicIndex.UPT_DATETIME, DSL.currentLocalDateTime())
                .onDuplicateKeyUpdate()
                .set(tEconomicIndex.URL, url)
                .set(tEconomicIndex.TITLE, title)
                .set(tEconomicIndex.PRICE, price)
                .set(tEconomicIndex.PRICE_CHANGE, priceChange)
                .set(tEconomicIndex.PRICE_CHANGE_PERCENT, priceChangePercent)
                .set(tEconomicIndex.UPT_ID, uptId)
                .set(tEconomicIndex.UPT_DATETIME, DSL.currentLocalDateTime())
                .execute();
    }

    public Optional<EconomicIndex> select(String code) {
        EconomicIndex queryResult = dslContext.select()
                .from(tEconomicIndex)
                .where(tEconomicIndex.CODE.equal(code))
                .fetchOneInto(EconomicIndex.class);

        return queryResult == null ? Optional.empty() : Optional.of(queryResult);
    }
}
