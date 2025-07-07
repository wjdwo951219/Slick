package toy.slick.repository.mysql.inheritable;

import lombok.NonNull;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.UpdateConditionStep;
import org.jooq.impl.TableImpl;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.List;

public abstract class QueryCRUD<R extends UpdatableRecordImpl<R>> {
    protected final DSLContext dslContext;

    public QueryCRUD(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    protected SelectConditionStep<Record> querySelect(@NonNull TableImpl<R> table, @NonNull Condition condition) {
        return dslContext
                .select()
                .from(table)
                .where(condition);
    }

    protected int querySelectCnt(@NonNull TableImpl<R> table, @NonNull Condition condition) {
        return dslContext
                .selectCount()
                .from(table)
                .where(condition)
                .fetch()
                .getFirst()
                .value1();
    }

    protected InsertSetMoreStep<R> queryInsert(@NonNull TableImpl<R> table, @NonNull R record) {
        return dslContext
                .insertInto(table)
                .set(record);
    }

    protected InsertSetMoreStep<R> queryInsert(@NonNull TableImpl<R> table, @NonNull List<R> recordList) {
        return dslContext
                .insertInto(table)
                .set(recordList);
    }

    protected DeleteConditionStep<R> queryDelete(@NonNull TableImpl<R> table, @NonNull Condition condition) {
        return dslContext
                .deleteFrom(table)
                .where(condition);
    }

    protected UpdateConditionStep<R> queryUpdate(@NonNull TableImpl<R> table, @NonNull R record, @NonNull Condition condition) {
        return dslContext
                .update(table)
                .set(record)
                .where(condition);
    }
}
