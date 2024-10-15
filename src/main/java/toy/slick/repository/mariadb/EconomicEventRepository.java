package toy.slick.repository.mariadb;

import org.jooq.DSLContext;
import org.jooq.generated.tables.JEconomicEvent;
import org.jooq.generated.tables.pojos.EconomicEvent;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class EconomicEventRepository {
    private final JEconomicEvent tEconomicEvent = JEconomicEvent.ECONOMIC_EVENT;
    private final DSLContext dslContext;

    public EconomicEventRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public int save(String id, LocalDateTime dateTime, String name, String country, String importance, String actual, String forecast, String previous, String regId, String uptId) {
        return dslContext.insertInto(tEconomicEvent)
                .set(tEconomicEvent.ID, id)
                .set(tEconomicEvent.DATETIME, dateTime)
                .set(tEconomicEvent.NAME, name)
                .set(tEconomicEvent.COUNTRY, country)
                .set(tEconomicEvent.IMPORTANCE, importance)
                .set(tEconomicEvent.ACTUAL, actual)
                .set(tEconomicEvent.FORECAST, forecast)
                .set(tEconomicEvent.PREVIOUS, previous)
                .set(tEconomicEvent.REG_ID, regId)
                .set(tEconomicEvent.REG_DATETIME, DSL.currentLocalDateTime())
                .set(tEconomicEvent.UPT_ID, uptId)
                .set(tEconomicEvent.UPT_DATETIME, DSL.currentLocalDateTime())
                .onDuplicateKeyUpdate()
                .set(tEconomicEvent.DATETIME, dateTime)
                .set(tEconomicEvent.NAME, name)
                .set(tEconomicEvent.COUNTRY, country)
                .set(tEconomicEvent.IMPORTANCE, importance)
                .set(tEconomicEvent.ACTUAL, actual)
                .set(tEconomicEvent.FORECAST, forecast)
                .set(tEconomicEvent.PREVIOUS, previous)
                .set(tEconomicEvent.UPT_ID, uptId)
                .set(tEconomicEvent.UPT_DATETIME, DSL.currentLocalDateTime())
                .execute();
    }

    public List<EconomicEvent> select(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return dslContext.select()
                .from(tEconomicEvent)
                .where(tEconomicEvent.DATETIME.greaterOrEqual(startDateTime),
                        tEconomicEvent.DATETIME.lessThan(endDateTime))
                .fetchInto(EconomicEvent.class);
    }

    public int delete(LocalDateTime untilDateTime) {
        return dslContext.delete(tEconomicEvent)
                .where(tEconomicEvent.DATETIME.lessThan(untilDateTime))
                .execute();
    }
}
