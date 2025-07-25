/*
 * This file is generated by jOOQ.
 */
package org.jooq.generated.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.generated.Indexes;
import org.jooq.generated.J_786b676a8e45;
import org.jooq.generated.Keys;
import org.jooq.generated.tables.records.EconomicEventRecord;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class JEconomicEvent extends TableImpl<EconomicEventRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>786b676a8e45.ECONOMIC_EVENT</code>
     */
    public static final JEconomicEvent ECONOMIC_EVENT = new JEconomicEvent();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EconomicEventRecord> getRecordType() {
        return EconomicEventRecord.class;
    }

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.ID</code>.
     */
    public final TableField<EconomicEventRecord, String> ID = createField(DSL.name("ID"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.URL</code>.
     */
    public final TableField<EconomicEventRecord, String> URL = createField(DSL.name("URL"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.DATETIME</code>.
     */
    public final TableField<EconomicEventRecord, LocalDateTime> DATETIME = createField(DSL.name("DATETIME"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.NAME</code>.
     */
    public final TableField<EconomicEventRecord, String> NAME = createField(DSL.name("NAME"), SQLDataType.VARCHAR(100).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.COUNTRY</code>.
     */
    public final TableField<EconomicEventRecord, String> COUNTRY = createField(DSL.name("COUNTRY"), SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.IMPORTANCE</code>.
     */
    public final TableField<EconomicEventRecord, String> IMPORTANCE = createField(DSL.name("IMPORTANCE"), SQLDataType.VARCHAR(10).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.ACTUAL</code>.
     */
    public final TableField<EconomicEventRecord, String> ACTUAL = createField(DSL.name("ACTUAL"), SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.FORECAST</code>.
     */
    public final TableField<EconomicEventRecord, String> FORECAST = createField(DSL.name("FORECAST"), SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.PREVIOUS</code>.
     */
    public final TableField<EconomicEventRecord, String> PREVIOUS = createField(DSL.name("PREVIOUS"), SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.REG_DATETIME</code>.
     */
    public final TableField<EconomicEventRecord, LocalDateTime> REG_DATETIME = createField(DSL.name("REG_DATETIME"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.REG_ID</code>.
     */
    public final TableField<EconomicEventRecord, String> REG_ID = createField(DSL.name("REG_ID"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.UPT_DATETIME</code>.
     */
    public final TableField<EconomicEventRecord, LocalDateTime> UPT_DATETIME = createField(DSL.name("UPT_DATETIME"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.ECONOMIC_EVENT.UPT_ID</code>.
     */
    public final TableField<EconomicEventRecord, String> UPT_ID = createField(DSL.name("UPT_ID"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    private JEconomicEvent(Name alias, Table<EconomicEventRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private JEconomicEvent(Name alias, Table<EconomicEventRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>786b676a8e45.ECONOMIC_EVENT</code> table
     * reference
     */
    public JEconomicEvent(String alias) {
        this(DSL.name(alias), ECONOMIC_EVENT);
    }

    /**
     * Create an aliased <code>786b676a8e45.ECONOMIC_EVENT</code> table
     * reference
     */
    public JEconomicEvent(Name alias) {
        this(alias, ECONOMIC_EVENT);
    }

    /**
     * Create a <code>786b676a8e45.ECONOMIC_EVENT</code> table reference
     */
    public JEconomicEvent() {
        this(DSL.name("ECONOMIC_EVENT"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : J_786b676a8e45._786B676A8E45;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.ECONOMIC_EVENT_ECONOMIC_EVENT_DATE_UTC_IDX);
    }

    @Override
    public UniqueKey<EconomicEventRecord> getPrimaryKey() {
        return Keys.KEY_ECONOMIC_EVENT_PRIMARY;
    }

    @Override
    public JEconomicEvent as(String alias) {
        return new JEconomicEvent(DSL.name(alias), this);
    }

    @Override
    public JEconomicEvent as(Name alias) {
        return new JEconomicEvent(alias, this);
    }

    @Override
    public JEconomicEvent as(Table<?> alias) {
        return new JEconomicEvent(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public JEconomicEvent rename(String name) {
        return new JEconomicEvent(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public JEconomicEvent rename(Name name) {
        return new JEconomicEvent(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public JEconomicEvent rename(Table<?> name) {
        return new JEconomicEvent(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JEconomicEvent where(Condition condition) {
        return new JEconomicEvent(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JEconomicEvent where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JEconomicEvent where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JEconomicEvent where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public JEconomicEvent where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public JEconomicEvent where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public JEconomicEvent where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public JEconomicEvent where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JEconomicEvent whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JEconomicEvent whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
