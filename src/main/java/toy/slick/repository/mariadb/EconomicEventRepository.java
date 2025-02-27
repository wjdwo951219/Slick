package toy.slick.repository.mariadb;

import lombok.NonNull;
import org.apache.commons.collections4.ListUtils;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JEconomicEvent;
import org.jooq.generated.tables.pojos.EconomicEvent;
import org.jooq.generated.tables.records.EconomicEventRecord;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mariadb.inheritable.QueryCRUD;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Repository
public class EconomicEventRepository extends QueryCRUD<EconomicEventRecord> {
    private final JEconomicEvent tEconomicEvent = JEconomicEvent.ECONOMIC_EVENT;

    public EconomicEventRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public List<EconomicEvent> select(@NonNull String country, @NonNull LocalDateTime startDateTime, @NonNull LocalDateTime endDateTime) {
        SelectConditionStep<Record> query = this.querySelect(
                tEconomicEvent,
                tEconomicEvent.COUNTRY.equal(country)
                        .and(tEconomicEvent.DATETIME.greaterOrEqual(startDateTime))
                        .and(tEconomicEvent.DATETIME.lessThan(endDateTime)));

        return query.fetchInto(EconomicEvent.class);
    }

    public int insertBatch(@NonNull List<EconomicEvent> economicEventList, int batchSize, @NonNull String regId) {
        int insertCnt = 0;

        for (List<EconomicEvent> partition : ListUtils.partition(economicEventList, batchSize)) {
            InsertSetMoreStep<EconomicEventRecord> query = this.queryInsert(
                    tEconomicEvent,
                    partition
                            .stream()
                            .map(economicEvent -> {
                                LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

                                return new EconomicEventRecord(
                                        economicEvent.getId(),
                                        economicEvent.getDatetime(),
                                        economicEvent.getName(),
                                        economicEvent.getCountry(),
                                        economicEvent.getImportance(),
                                        economicEvent.getActual(),
                                        economicEvent.getForecast(),
                                        economicEvent.getPrevious(),
                                        now,
                                        regId,
                                        now,
                                        regId);
                            })
                            .toList());

            insertCnt += query.execute();
        }

        return insertCnt;
    }

    public int delete(@NonNull LocalDateTime untilDateTime) {
        DeleteConditionStep<EconomicEventRecord> query = this.queryDelete(
                tEconomicEvent,
                tEconomicEvent.DATETIME.lessThan(untilDateTime));

        return query.execute();
    }

    public int delete(@NonNull Set<String> idSet) {
        DeleteConditionStep<EconomicEventRecord> query = this.queryDelete(
                tEconomicEvent,
                tEconomicEvent.ID.in(idSet));

        return query.execute();
    }
}
