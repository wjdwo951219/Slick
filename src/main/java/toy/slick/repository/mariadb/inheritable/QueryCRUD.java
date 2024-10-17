package toy.slick.repository.mariadb.inheritable;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.UpdateConditionStep;
import org.jooq.impl.TableImpl;
import org.jooq.impl.UpdatableRecordImpl;

public abstract class QueryCRUD<R extends UpdatableRecordImpl<R>> {
    protected final DSLContext dslContext;

    public QueryCRUD(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    protected SelectConditionStep<Record> querySelect(TableImpl<R> table, Condition condition) {
        return dslContext
                .select()
                .from(table)
                .where(condition);
    }

    protected int querySelectCnt(TableImpl<R> table, Condition condition) {
        return dslContext
                .selectCount()
                .from(table)
                .where(condition)
                .fetch()
                .get(0)
                .value1();
    }

    protected InsertSetMoreStep<R> queryInsert(TableImpl<R> table, R record) {
        return dslContext
                .insertInto(table)
                .set(record);
    }

    protected DeleteConditionStep<R> queryDelete(TableImpl<R> table, Condition condition) {
        return dslContext
                .deleteFrom(table)
                .where(condition);
    }

    protected UpdateConditionStep<R> queryUpdate(TableImpl<R> table, R record, Condition condition) {
        return dslContext
                .update(table)
                .set(record)
                .where(condition);
    }
}
