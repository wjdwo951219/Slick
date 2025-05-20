package toy.slick.repository.mysql;

import lombok.NonNull;
import org.apache.commons.collections4.ListUtils;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.generated.tables.JHoliday;
import org.jooq.generated.tables.pojos.Holiday;
import org.jooq.generated.tables.records.HolidayRecord;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import toy.slick.common.Const;
import toy.slick.repository.mysql.inheritable.QueryCRUD;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Repository
public class HolidayRepository extends QueryCRUD<HolidayRecord> {
    private final JHoliday tHoliday = JHoliday.HOLIDAY;

    public HolidayRepository(DSLContext dslContext) {
        super(dslContext);
    }

    public int insertBatch(@NonNull List<Holiday> holidayList, int batchSize, @NonNull String regId) {
        int insertCnt = 0;

        for (List<Holiday> partition : ListUtils.partition(holidayList, batchSize)) {
            InsertSetMoreStep<HolidayRecord> query = this.queryInsert(
                    tHoliday,
                    partition
                            .stream()
                            .map(holiday -> {
                                LocalDateTime now = LocalDateTime.now(ZoneId.of(Const.ZoneId.UTC));

                                return new HolidayRecord(
                                        holiday.getCountry(),
                                        holiday.getDate(),
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

    public Optional<Holiday> select(@NonNull String country, @NonNull LocalDate date) {
        SelectConditionStep<Record> query = this.querySelect(
                tHoliday,
                tHoliday.COUNTRY.equal(country)
                        .and(tHoliday.DATE.equal(date)));

        return Optional.ofNullable(query.fetchOneInto(Holiday.class));
    }

    public int deleteAll() {
        DeleteConditionStep<HolidayRecord> query = this.queryDelete(
                tHoliday,
                DSL.noCondition());

        return query.execute();
    }
}
