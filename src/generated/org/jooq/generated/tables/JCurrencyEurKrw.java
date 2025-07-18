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
import org.jooq.generated.tables.records.CurrencyEurKrwRecord;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class JCurrencyEurKrw extends TableImpl<CurrencyEurKrwRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>786b676a8e45.CURRENCY_EUR_KRW</code>
     */
    public static final JCurrencyEurKrw CURRENCY_EUR_KRW = new JCurrencyEurKrw();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CurrencyEurKrwRecord> getRecordType() {
        return CurrencyEurKrwRecord.class;
    }

    /**
     * The column <code>786b676a8e45.CURRENCY_EUR_KRW.PRICE</code>.
     */
    public final TableField<CurrencyEurKrwRecord, String> PRICE = createField(DSL.name("PRICE"), SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.CURRENCY_EUR_KRW.PRICE_CHANGE</code>.
     */
    public final TableField<CurrencyEurKrwRecord, String> PRICE_CHANGE = createField(DSL.name("PRICE_CHANGE"), SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * The column
     * <code>786b676a8e45.CURRENCY_EUR_KRW.PRICE_CHANGE_PERCENT</code>.
     */
    public final TableField<CurrencyEurKrwRecord, String> PRICE_CHANGE_PERCENT = createField(DSL.name("PRICE_CHANGE_PERCENT"), SQLDataType.VARCHAR(50).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.CURRENCY_EUR_KRW.DATETIME</code>.
     */
    public final TableField<CurrencyEurKrwRecord, LocalDateTime> DATETIME = createField(DSL.name("DATETIME"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.CURRENCY_EUR_KRW.URL</code>.
     */
    public final TableField<CurrencyEurKrwRecord, String> URL = createField(DSL.name("URL"), SQLDataType.VARCHAR(500).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.CURRENCY_EUR_KRW.REG_DATETIME</code>.
     */
    public final TableField<CurrencyEurKrwRecord, LocalDateTime> REG_DATETIME = createField(DSL.name("REG_DATETIME"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.CURRENCY_EUR_KRW.REG_ID</code>.
     */
    public final TableField<CurrencyEurKrwRecord, String> REG_ID = createField(DSL.name("REG_ID"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.CURRENCY_EUR_KRW.UPT_DATETIME</code>.
     */
    public final TableField<CurrencyEurKrwRecord, LocalDateTime> UPT_DATETIME = createField(DSL.name("UPT_DATETIME"), SQLDataType.LOCALDATETIME(0).nullable(false), this, "");

    /**
     * The column <code>786b676a8e45.CURRENCY_EUR_KRW.UPT_ID</code>.
     */
    public final TableField<CurrencyEurKrwRecord, String> UPT_ID = createField(DSL.name("UPT_ID"), SQLDataType.VARCHAR(200).nullable(false), this, "");

    private JCurrencyEurKrw(Name alias, Table<CurrencyEurKrwRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private JCurrencyEurKrw(Name alias, Table<CurrencyEurKrwRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>786b676a8e45.CURRENCY_EUR_KRW</code> table
     * reference
     */
    public JCurrencyEurKrw(String alias) {
        this(DSL.name(alias), CURRENCY_EUR_KRW);
    }

    /**
     * Create an aliased <code>786b676a8e45.CURRENCY_EUR_KRW</code> table
     * reference
     */
    public JCurrencyEurKrw(Name alias) {
        this(alias, CURRENCY_EUR_KRW);
    }

    /**
     * Create a <code>786b676a8e45.CURRENCY_EUR_KRW</code> table reference
     */
    public JCurrencyEurKrw() {
        this(DSL.name("CURRENCY_EUR_KRW"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : J_786b676a8e45._786B676A8E45;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.asList(Indexes.CURRENCY_EUR_KRW_CURRENCY_EUR_KRW_DATETIME_IDX);
    }

    @Override
    public UniqueKey<CurrencyEurKrwRecord> getPrimaryKey() {
        return Keys.KEY_CURRENCY_EUR_KRW_PRIMARY;
    }

    @Override
    public JCurrencyEurKrw as(String alias) {
        return new JCurrencyEurKrw(DSL.name(alias), this);
    }

    @Override
    public JCurrencyEurKrw as(Name alias) {
        return new JCurrencyEurKrw(alias, this);
    }

    @Override
    public JCurrencyEurKrw as(Table<?> alias) {
        return new JCurrencyEurKrw(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public JCurrencyEurKrw rename(String name) {
        return new JCurrencyEurKrw(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public JCurrencyEurKrw rename(Name name) {
        return new JCurrencyEurKrw(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public JCurrencyEurKrw rename(Table<?> name) {
        return new JCurrencyEurKrw(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JCurrencyEurKrw where(Condition condition) {
        return new JCurrencyEurKrw(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JCurrencyEurKrw where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JCurrencyEurKrw where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JCurrencyEurKrw where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public JCurrencyEurKrw where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public JCurrencyEurKrw where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public JCurrencyEurKrw where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public JCurrencyEurKrw where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JCurrencyEurKrw whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public JCurrencyEurKrw whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
