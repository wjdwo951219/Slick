/*
 * This file is generated by jOOQ.
 */
package org.jooq.generated;


import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.generated.tables.JCurrencyEurKrw;
import org.jooq.generated.tables.JCurrencyJpyKrw;
import org.jooq.generated.tables.JCurrencyUsdKrw;
import org.jooq.generated.tables.JDji;
import org.jooq.generated.tables.JEconomicEvent;
import org.jooq.generated.tables.JFearAndGreed;
import org.jooq.generated.tables.JIxic;
import org.jooq.generated.tables.JKosdaq;
import org.jooq.generated.tables.JKospi;
import org.jooq.generated.tables.JSpx;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class J_786b676a8e45 extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>786b676a8e45</code>
     */
    public static final J_786b676a8e45 _786B676A8E45 = new J_786b676a8e45();

    /**
     * The table <code>786b676a8e45.CURRENCY_EUR_KRW</code>.
     */
    public final JCurrencyEurKrw CURRENCY_EUR_KRW = JCurrencyEurKrw.CURRENCY_EUR_KRW;

    /**
     * The table <code>786b676a8e45.CURRENCY_JPY_KRW</code>.
     */
    public final JCurrencyJpyKrw CURRENCY_JPY_KRW = JCurrencyJpyKrw.CURRENCY_JPY_KRW;

    /**
     * The table <code>786b676a8e45.CURRENCY_USD_KRW</code>.
     */
    public final JCurrencyUsdKrw CURRENCY_USD_KRW = JCurrencyUsdKrw.CURRENCY_USD_KRW;

    /**
     * The table <code>786b676a8e45.DJI</code>.
     */
    public final JDji DJI = JDji.DJI;

    /**
     * The table <code>786b676a8e45.ECONOMIC_EVENT</code>.
     */
    public final JEconomicEvent ECONOMIC_EVENT = JEconomicEvent.ECONOMIC_EVENT;

    /**
     * The table <code>786b676a8e45.FEAR_AND_GREED</code>.
     */
    public final JFearAndGreed FEAR_AND_GREED = JFearAndGreed.FEAR_AND_GREED;

    /**
     * The table <code>786b676a8e45.IXIC</code>.
     */
    public final JIxic IXIC = JIxic.IXIC;

    /**
     * The table <code>786b676a8e45.KOSDAQ</code>.
     */
    public final JKosdaq KOSDAQ = JKosdaq.KOSDAQ;

    /**
     * The table <code>786b676a8e45.KOSPI</code>.
     */
    public final JKospi KOSPI = JKospi.KOSPI;

    /**
     * The table <code>786b676a8e45.SPX</code>.
     */
    public final JSpx SPX = JSpx.SPX;

    /**
     * No further instances allowed
     */
    private J_786b676a8e45() {
        super("786b676a8e45", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            JCurrencyEurKrw.CURRENCY_EUR_KRW,
            JCurrencyJpyKrw.CURRENCY_JPY_KRW,
            JCurrencyUsdKrw.CURRENCY_USD_KRW,
            JDji.DJI,
            JEconomicEvent.ECONOMIC_EVENT,
            JFearAndGreed.FEAR_AND_GREED,
            JIxic.IXIC,
            JKosdaq.KOSDAQ,
            JKospi.KOSPI,
            JSpx.SPX
        );
    }
}
