/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (HijriCalendar.java) is part of project Time4J.
 *
 * Time4J is free software: You can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * Time4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Time4J. If not, see <http://www.gnu.org/licenses/>.
 * -----------------------------------------------------------------------
 */

package net.time4j.calendar;

import net.time4j.ClockUnit;
import net.time4j.PlainTimestamp;
import net.time4j.Weekday;
import net.time4j.Weekmodel;
import net.time4j.base.MathUtils;
import net.time4j.base.TimeSource;
import net.time4j.calendar.service.StdEnumDateElement;
import net.time4j.calendar.service.StdIntegerDateElement;
import net.time4j.calendar.service.StdWeekdayElement;
import net.time4j.engine.AttributeQuery;
import net.time4j.engine.CalendarDays;
import net.time4j.engine.CalendarFamily;
import net.time4j.engine.CalendarVariant;
import net.time4j.engine.ChronoDisplay;
import net.time4j.engine.ChronoElement;
import net.time4j.engine.ChronoEntity;
import net.time4j.engine.ChronoException;
import net.time4j.engine.ChronoMerger;
import net.time4j.engine.Chronology;
import net.time4j.engine.ElementRule;
import net.time4j.engine.FormattableElement;
import net.time4j.engine.StartOfDay;
import net.time4j.engine.ValidationElement;
import net.time4j.format.Attributes;
import net.time4j.format.CalendarType;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>Represents the Hijri calendar used in many islamic countries. </p>
 *
 * <p>It is a lunar calendar which exists in several variants and is mainly for religious purposes.
 * The variant used in Saudi-Arabia is named &quot;islamic-umalqura&quot; and is based on data partially
 * observed by sighting the new moon, partially by astronomical calculations/predictions. Note that the
 * religious authorities in most countries often publish dates which deviate from such official calendars
 * by one or two days. </p>
 *
 * <p>The calendar year is divided into 12 islamic months. Every month has either 29 or 30 days. The length
 * of the month in days shall reflect the date when the new moon appears. However, for every variant there
 * are different data or rules how to determine if a month has 29 or 30 days. The Hijri calendar day starts
 * in the evening. </p>
 *
 * <p>Following elements which are declared as constants are registered by
 * this class: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Example of usage: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;HijriCalendar&gt; formatter =
 *       ChronoFormatter.setUp(HijriCalendar.class, Locale.ENGLISH)
 *       .addPattern(&quot;EEE, d. MMMM yy&quot;, PatternType.GENERIC).build()
 *       .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA)
 *       .with(Attributes.PIVOT_YEAR, 1500); // mapped to range 1400-1499
 *     HijriCalendar hijri = formatter.parse(&quot;Thu, 29. Ramadan 36&quot;);
 *     PlainDate date = hijri.transform(PlainDate.class);
 *     System.out.println(date); // 2015-07-16
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 * @doctags.experimental The serialization format will change in future.
 * @doctags.concurrency {immutable}
 */
/*[deutsch]
 * <p>Repr&auml;sentiert den Hijri-Kalender, der in vielen islamischen L&auml;ndern vorwiegend f&uuml;r
 * religi&ouml;se Zwecke benutzt wird. </p>
 *
 * <p>Es handelt sich um einen lunaren Kalender, der in verschiedenen Varianten existiert. Die Variante
 * in Saudi-Arabien hei&szlig;t &quot;islamic-umalqura&quot; und basiert teilweise auf Daten gewonnen
 * durch die Sichtung des Neumonds, teilweise auf astronomischen Berechnungen und Voraussagen. Zu beachten:
 * Die religi&ouml;sen Autorit&auml;ten in den meisten L&auml;ndern folgen nicht streng den offiziellen
 * Kalendervarianten, sondern ver&ouml;ffentlichen oft ein Datum, das 1 oder 2 Tage abweichen kann. </p>
 *
 * <p>Das Kalendarjahr wird in 12 islamische Monate geteilt. Jeder Monat hat entweder 29 oder 30 Tage. Die
 * L&auml;nge des Monats in Tagen soll den Zeitpunkt reflektieren, wann der Neumond gesichtet wird. Aber
 * jede Variante kennt verschiedenen Daten oder Regeln, um zu bestimmen, ob ein Monat 29 oder 30 Tage hat.
 * Der islamische Kalendertag startet am Abend. </p>
 *
 * <p>Registriert sind folgende als Konstanten deklarierte Elemente: </p>
 *
 * <ul>
 *  <li>{@link #DAY_OF_WEEK}</li>
 *  <li>{@link #DAY_OF_MONTH}</li>
 *  <li>{@link #DAY_OF_YEAR}</li>
 *  <li>{@link #MONTH_OF_YEAR}</li>
 *  <li>{@link #YEAR_OF_ERA}</li>
 *  <li>{@link #ERA}</li>
 * </ul>
 *
 * <p>Anwendungsbeispiel: </p>
 *
 * <pre>
 *     ChronoFormatter&lt;HijriCalendar&gt; formatter =
 *       ChronoFormatter.setUp(HijriCalendar.class, Locale.ENGLISH)
 *       .addPattern(&quot;EEE, d. MMMM yy&quot;, PatternType.GENERIC).build()
 *       .withCalendarVariant(HijriCalendar.VARIANT_UMALQURA)
 *       .with(Attributes.PIVOT_YEAR, 1500); // mapped to range 1400-1499
 *     HijriCalendar hijri = formatter.parse(&quot;Thu, 29. Ramadan 36&quot;);
 *     PlainDate date = hijri.transform(PlainDate.class);
 *     System.out.println(date); // 2015-07-16
 * </pre>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 * @doctags.experimental The serialization format will change in future.
 * @doctags.concurrency {immutable}
 */
@CalendarType("islamic")
public final class HijriCalendar
    extends CalendarVariant<HijriCalendar> {

    //~ Statische Felder/Initialisierungen --------------------------------

    private static final int YEAR_INDEX = 0;
    private static final int DAY_OF_MONTH_INDEX = 2;
    private static final int DAY_OF_YEAR_INDEX = 3;

    /**
     * <p>Represents the islamic era. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert die islamische &Auml;ra. </p>
     */
    @FormattableElement(format = "G")
    public static final StdCalendarElement<HijriEra, HijriCalendar> ERA =
        new StdEnumDateElement<>("ERA", HijriCalendar.class, HijriEra.class, 'G');

    /**
     * <p>Represents the islamic year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert das islamische Jahr. </p>
     */
    @FormattableElement(format = "y")
    public static final StdCalendarElement<Integer, HijriCalendar> YEAR_OF_ERA =
        new StdIntegerDateElement<>("YEAR_OF_ERA", HijriCalendar.class, Integer.MIN_VALUE, Integer.MAX_VALUE, 'y');

    /**
     * <p>Represents the islamic month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den islamischen Monat. </p>
     */
    @FormattableElement(format = "M", standalone = "L")
    public static final StdCalendarElement<HijriMonth, HijriCalendar> MONTH_OF_YEAR =
        new StdEnumDateElement<>("MONTH_OF_YEAR", HijriCalendar.class, HijriMonth.class, 'M');

    /**
     * <p>Represents the islamic day of month. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den islamischen Tag des Monats. </p>
     */
    @FormattableElement(format = "d")
    public static final StdCalendarElement<Integer, HijriCalendar> DAY_OF_MONTH =
        new StdIntegerDateElement<>("DAY_OF_MONTH", HijriCalendar.class, 1, 30, 'd');

    /**
     * <p>Represents the islamic day of year. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den islamischen Tag des Jahres. </p>
     */
    @FormattableElement(format = "D")
    public static final StdCalendarElement<Integer, HijriCalendar> DAY_OF_YEAR =
        new StdIntegerDateElement<>("DAY_OF_YEAR", HijriCalendar.class, 1, 355, 'D');

    /**
     * <p>Represents the islamic day of week. </p>
     *
     * <p>If the day-of-week is set to a new value then Time4J handles the islamic calendar week
     * as starting on Sunday. </p>
     */
    /*[deutsch]
     * <p>Repr&auml;sentiert den islamischen Tag der Woche. </p>
     *
     * <p>Wenn der Tag der Woche auf einen neuen Wert gesetzt wird, behandelt Time4J die islamische
     * Kalenderwoche so, da&szlig; sie am Sonntag beginnt. </p>
     */
    @FormattableElement(format = "E")
    public static final StdCalendarElement<Weekday, HijriCalendar> DAY_OF_WEEK =
        new StdWeekdayElement<>(HijriCalendar.class);

    /**
     * The name of Umm-al-qura-variant.
     */
    /*[deutsch]
     * Der Name der Umm-al-qura-Variante.
     */
    public static final String VARIANT_UMALQURA = "islamic-umalqura";

    private static final Map<String, MonthBasedCalendarSystem<HijriCalendar>> CALSYS;
    private static final CalendarFamily<HijriCalendar> ENGINE;

    static {
        Map<String, MonthBasedCalendarSystem<HijriCalendar>> calsys = new VariantMap();
        calsys.put(VARIANT_UMALQURA, AstronomicalHijriData.UMALQURA);
        CALSYS = calsys;

        CalendarFamily.Builder<HijriCalendar> builder =
            CalendarFamily.Builder.setUp(
                HijriCalendar.class,
                new Merger(),
                CALSYS)
            .appendElement(
                ERA,
                new EraRule())
            .appendElement(
                YEAR_OF_ERA,
                new IntegerRule(YEAR_INDEX))
            .appendElement(
                MONTH_OF_YEAR,
                new MonthRule())
            .appendElement(
                DAY_OF_MONTH,
                new IntegerRule(DAY_OF_MONTH_INDEX))
            .appendElement(
                DAY_OF_YEAR,
                new IntegerRule(DAY_OF_YEAR_INDEX))
            .appendElement(
                DAY_OF_WEEK,
                new WeekdayRule()
            );
        ENGINE = builder.build();
    }

    //~ Instanzvariablen --------------------------------------------------

    private final int hyear;
    private final int hmonth;
    private final int hdom;
    private final String variant;

    //~ Konstruktoren -----------------------------------------------------

    private HijriCalendar(
        int hyear,
        int hmonth,
        int hdom,
        String variant
    ) {
        super();

        this.hyear = hyear;
        this.hmonth = hmonth;
        this.hdom = hdom;
        this.variant = variant;

    }

    //~ Methoden ----------------------------------------------------------

    /**
     * <p>Creates a new instance of a Hijri calendar date in given variant. </p>
     *
     * @param   variant calendar variant
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Hijri-Kalenderdatum in der angegebenen Variante. </p>
     *
     * @param   variant calendar variant
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    public static HijriCalendar of(
        String variant,
        int hyear,
        HijriMonth hmonth,
        int hdom
    ) {

        return HijriCalendar.of(variant, hyear, hmonth.getValue(), hdom);

    }

    /**
     * <p>Creates a new instance of a Hijri calendar date in given variant. </p>
     *
     * @param   variant calendar variant
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Hijri-Kalenderdatum in der angegebenen Variante. </p>
     *
     * @param   variant calendar variant
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    public static HijriCalendar of(
        String variant,
        int hyear,
        int hmonth,
        int hdom
    ) {

        MonthBasedCalendarSystem<HijriCalendar> calsys = getCalendarSystem(variant);

        if (!calsys.isValid(HijriEra.ANNO_HEGIRAE, hyear, hmonth, hdom)) {
            throw new IllegalArgumentException(
                "Invalid hijri date: year=" + hyear + ", month=" + hmonth + ", day=" + hdom);
        }

        return new HijriCalendar(hyear, hmonth, hdom, variant);

    }

    /**
     * <p>Creates a new instance of a Hijri calendar date in the variant &quot;islamic-umalqura&quot;
     * used in Saudi-Arabia. </p>
     *
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Hijri-Kalenderdatum in der Variante &quot;islamic-umalqura&quot;, die in
     * Saudi-Arabien benutzt wird. </p>
     *
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    public static HijriCalendar ofUmalqura(
        int hyear,
        HijriMonth hmonth,
        int hdom
    ) {

        return HijriCalendar.of(VARIANT_UMALQURA, hyear, hmonth.getValue(), hdom);

    }

    /**
     * <p>Creates a new instance of a Hijri calendar date in the variant &quot;islamic-umalqura&quot;
     * used in Saudi-Arabia. </p>
     *
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  ChronoException if given variant is not supported
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Erzeugt ein neues Hijri-Kalenderdatum in der Variante &quot;islamic-umalqura&quot;, die in
     * Saudi-Arabien benutzt wird. </p>
     *
     * @param   hyear   islamic year
     * @param   hmonth  islamic month
     * @param   hdom    islamic day of month
     * @return  new instance of {@code HijriCalendar}
     * @throws  IllegalArgumentException in case of any inconsistencies
     * @since   3.5/4.3
     */
    public static HijriCalendar ofUmalqura(
        int hyear,
        int hmonth,
        int hdom
    ) {

        return HijriCalendar.of(VARIANT_UMALQURA, hyear, hmonth, hdom);

    }

    /**
     * <p>Yields the islamic era. </p>
     *
     * @return  {@link HijriEra#ANNO_HEGIRAE}
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert die islamische &Auml;ra. </p>
     *
     * @return  {@link HijriEra#ANNO_HEGIRAE}
     * @since   3.5/4.3
     */
    public HijriEra getEra() {

        return HijriEra.ANNO_HEGIRAE;

    }

    /**
     * <p>Yields the islamic year. </p>
     *
     * @return  int
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert das islamische Jahr. </p>
     *
     * @return  int
     * @since   3.5/4.3
     */
    public int getYear() {

        return this.hyear;

    }

    /**
     * <p>Yields the islamic month. </p>
     *
     * @return  enum
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert den islamischen Monat. </p>
     *
     * @return  enum
     * @since   3.5/4.3
     */
    public HijriMonth getMonth() {

        return HijriMonth.valueOf(this.hmonth);

    }

    /**
     * <p>Yields the islamic day of month. </p>
     *
     * @return  int
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert den islamischen Tag des Monats. </p>
     *
     * @return  int
     * @since   3.5/4.3
     */
    public int getDayOfMonth() {

        return this.hdom;

    }

    /**
     * <p>Determines the day of week. </p>
     *
     * <p>The Hijri calendar also uses a 7-day-week. </p>
     *
     * @return  Weekday
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Ermittelt den Wochentag. </p>
     *
     * <p>Der Hijri-Kalendar verwendet ebenfalls eine 7-Tage-Woche. </p>
     *
     * @return  Weekday
     * @since   3.5/4.3
     */
    public Weekday getDayOfWeek() {

        long utcDays = getCalendarSystem(variant).transform(this);
        return Weekday.valueOf(MathUtils.floorModulo(utcDays + 5, 7) + 1);

    }

    /**
     * <p>Yields the islamic day of year. </p>
     *
     * @return  int
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert den islamischen Tag des Jahres. </p>
     *
     * @return  int
     * @since   3.5/4.3
     */
    public int getDayOfYear() {

        return this.get(DAY_OF_YEAR).intValue();

    }

    @Override
    public String getVariant() {

        return this.variant;

    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj instanceof HijriCalendar) {
            HijriCalendar that = (HijriCalendar) obj;
            return (
                (this.hdom == that.hdom)
                && (this.hmonth == that.hmonth)
                && (this.hyear == that.hyear)
                && this.variant.equals(that.variant)
            );
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {

        return (17 * this.hdom + 31 * this.hmonth + 37 * this.hyear) ^ this.variant.hashCode();

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder(32);
        sb.append("AH-");
        String y = String.valueOf(this.hyear);
        for (int i = y.length(); i < 4; i++) {
            sb.append('0');
        }
        sb.append(y);
        sb.append('-');
        if (this.hmonth < 10) {
            sb.append('0');
        }
        sb.append(this.hmonth);
        sb.append('-');
        if (this.hdom < 10) {
            sb.append('0');
        }
        sb.append(this.hdom);
        sb.append('[');
        sb.append(this.variant);
        sb.append(']');
        return sb.toString();

    }

    /**
     * <p>Returns the associated calendar family. </p>
     *
     * @return  chronology as calendar family
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Liefert die zugeh&ouml;rige Kalenderfamilie. </p>
     *
     * @return  chronology as calendar family
     * @since   3.5/4.3
     */
    public static CalendarFamily<HijriCalendar> family() {

        return ENGINE;

    }

    @Override
    protected CalendarFamily<HijriCalendar> getChronology() {

        return ENGINE;

    }

    @Override
    protected HijriCalendar getContext() {

        return this;

    }

    private MonthBasedCalendarSystem<HijriCalendar> getCalendarSystem() {

        return getCalendarSystem(this.variant);

    }

    private static MonthBasedCalendarSystem<HijriCalendar> getCalendarSystem(String variant) {

        MonthBasedCalendarSystem<HijriCalendar> calsys = CALSYS.get(variant);

        if (calsys == null) {
            throw new ChronoException("Unsupported calendar variant: " + variant);
        }

        return calsys;

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class VariantMap
        extends ConcurrentHashMap<String, MonthBasedCalendarSystem<HijriCalendar>> {

        //~ Methoden ------------------------------------------------------

        @Override
        public MonthBasedCalendarSystem<HijriCalendar> get(Object key) {

            MonthBasedCalendarSystem<HijriCalendar> calsys = super.get(key);

            if (calsys == null) {
                String variant = key.toString();

                if (key.equals(VARIANT_UMALQURA)) {
                    calsys = AstronomicalHijriData.UMALQURA;
                } else {
                    try {
                        calsys = new AstronomicalHijriData(variant);
                    } catch (IOException ioe) {
                        return null;
                    }
                }

                MonthBasedCalendarSystem<HijriCalendar> old = this.putIfAbsent(variant, calsys);

                if (old != null) {
                    calsys = old;
                }
            }

            return calsys;

        }

    }

    private static class IntegerRule
        implements ElementRule<HijriCalendar, Integer> {

        //~ Instanzvariablen ----------------------------------------------

        private final int index;

        //~ Konstruktoren -------------------------------------------------

        IntegerRule(int index) {
            super();

            this.index = index;

        }

        //~ Methoden ------------------------------------------------------

        @Override
        public Integer getValue(HijriCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    return context.hyear;
                case DAY_OF_MONTH_INDEX:
                    return context.hdom;
                case DAY_OF_YEAR_INDEX:
                    int doy = 0;
                    MonthBasedCalendarSystem<HijriCalendar> calsys = context.getCalendarSystem();
                    for (int m = 1; m < context.hmonth; m++) {
                        doy += calsys.getLengthOfMonth(HijriEra.ANNO_HEGIRAE, context.hyear, m);
                    }
                    return doy + context.hdom;
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public Integer getMinimum(HijriCalendar context) {

            switch (this.index) {
                case YEAR_INDEX:
                    MonthBasedCalendarSystem<HijriCalendar> calsys = context.getCalendarSystem();
                    return calsys.transform(calsys.getMinimumSinceUTC()).hyear;
                case DAY_OF_MONTH_INDEX:
                case DAY_OF_YEAR_INDEX:
                    return Integer.valueOf(1);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public Integer getMaximum(HijriCalendar context) {

            MonthBasedCalendarSystem<HijriCalendar> calsys = context.getCalendarSystem();

            switch (this.index) {
                case YEAR_INDEX:
                    return calsys.transform(calsys.getMaximumSinceUTC()).hyear;
                case DAY_OF_MONTH_INDEX:
                    return calsys.getLengthOfMonth(HijriEra.ANNO_HEGIRAE, context.hyear, context.hmonth);
                case DAY_OF_YEAR_INDEX:
                    int max = 0;
                    for (int m = 1; m <= 12; m++) {
                        max += calsys.getLengthOfMonth(HijriEra.ANNO_HEGIRAE, context.hyear, m);
                    }
                    return Integer.valueOf(max);
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public boolean isValid(
            HijriCalendar context,
            Integer value
        ) {

            if (value == null) {
                return false;
            }

            Integer min = this.getMinimum(context);
            Integer max = this.getMaximum(context);
            return ((min.compareTo(value) <= 0) && (max.compareTo(value) >= 0));

        }

        @Override
        public HijriCalendar withValue(
            HijriCalendar context,
            Integer value,
            boolean lenient
        ) {

            MonthBasedCalendarSystem<HijriCalendar> calsys = context.getCalendarSystem();

            if (
                lenient && (
                    (this.index == DAY_OF_MONTH_INDEX)
                    || (this.index == DAY_OF_YEAR_INDEX)
                )
            ) {
                long delta = MathUtils.safeSubtract(value.longValue(), this.getValue(context).longValue());
                return context.plus(CalendarDays.of(delta));
            }

            if (!this.isValid(context, value)) {
                throw new IllegalArgumentException("Out of range: " + value);
            }

            switch (this.index) {
                case YEAR_INDEX:
                    int y = value.intValue();
                    int dmax = calsys.getLengthOfMonth(HijriEra.ANNO_HEGIRAE, y, context.hmonth);
                    int d = Math.min(context.hdom, dmax);
                    return HijriCalendar.of(context.getVariant(), y, context.hmonth, d);
                case DAY_OF_MONTH_INDEX:
                    return new HijriCalendar(context.hyear, context.hmonth, value.intValue(), context.getVariant());
                case DAY_OF_YEAR_INDEX:
                    int delta = value.intValue() - this.getValue(context).intValue();
                    return context.plus(CalendarDays.of(delta));
                default:
                    throw new UnsupportedOperationException("Unknown element index: " + this.index);
            }

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HijriCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HijriCalendar context) {

            if (this.index == YEAR_INDEX) {
                return MONTH_OF_YEAR;
            }

            return null;

        }

    }

    private static class MonthRule
        implements ElementRule<HijriCalendar, HijriMonth> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HijriMonth getValue(HijriCalendar context) {

            return context.getMonth();

        }

        @Override
        public HijriMonth getMinimum(HijriCalendar context) {

            return HijriMonth.MUHARRAM;

        }

        @Override
        public HijriMonth getMaximum(HijriCalendar context) {

            return HijriMonth.DHU_AL_HIJJAH;

        }

        @Override
        public boolean isValid(
            HijriCalendar context,
            HijriMonth value
        ) {

            return (value != null);

        }

        @Override
        public HijriCalendar withValue(
            HijriCalendar context,
            HijriMonth value,
            boolean lenient
        ) {

            int m = value.getValue();
            int dmax = context.getCalendarSystem().getLengthOfMonth(HijriEra.ANNO_HEGIRAE, context.hyear, m);
            int d = Math.min(context.hdom, dmax);
            return new HijriCalendar(context.hyear, m, d, context.getVariant());

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HijriCalendar context) {

            return DAY_OF_MONTH;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HijriCalendar context) {

            return DAY_OF_MONTH;

        }

    }

    private static class EraRule
        implements ElementRule<HijriCalendar, HijriEra> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HijriEra getValue(HijriCalendar context) {

            return HijriEra.ANNO_HEGIRAE;

        }

        @Override
        public HijriEra getMinimum(HijriCalendar context) {

            return HijriEra.ANNO_HEGIRAE;

        }

        @Override
        public HijriEra getMaximum(HijriCalendar context) {

            return HijriEra.ANNO_HEGIRAE;

        }

        @Override
        public boolean isValid(
            HijriCalendar context,
            HijriEra value
        ) {

            return (value != null);

        }

        @Override
        public HijriCalendar withValue(
            HijriCalendar context,
            HijriEra value,
            boolean lenient
        ) {

            if (value == null) {
                throw new IllegalArgumentException("Missing era value.");
            }

            return context;

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HijriCalendar context) {

            return YEAR_OF_ERA;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HijriCalendar context) {

            return YEAR_OF_ERA;

        }

    }

    private static class WeekdayRule
        implements ElementRule<HijriCalendar, Weekday> {

        //~ Methoden ------------------------------------------------------

        @Override
        public Weekday getValue(HijriCalendar context) {

            return context.getDayOfWeek();

        }

        @Override
        public Weekday getMinimum(HijriCalendar context) {

            return Weekday.SUNDAY;

        }

        @Override
        public Weekday getMaximum(HijriCalendar context) {

            return Weekday.SATURDAY;

        }

        @Override
        public boolean isValid(
            HijriCalendar context,
            Weekday value
        ) {

            return (value != null);

        }

        @Override
        public HijriCalendar withValue(
            HijriCalendar context,
            Weekday value,
            boolean lenient
        ) {

            Weekmodel model = Weekmodel.of(Weekday.SUNDAY, 1);
            int oldValue = context.getDayOfWeek().getValue(model);
            int newValue = value.getValue(model);
            return context.plus(CalendarDays.of(newValue - oldValue));

        }

        @Override
        public ChronoElement<?> getChildAtFloor(HijriCalendar context) {

            return null;

        }

        @Override
        public ChronoElement<?> getChildAtCeiling(HijriCalendar context) {

            return null;

        }

    }

    private static class Merger
        implements ChronoMerger<HijriCalendar> {

        //~ Methoden ------------------------------------------------------

        @Override
        public HijriCalendar createFrom(
            TimeSource<?> clock,
            AttributeQuery attributes
        ) {

            PlainTimestamp tsp = PlainTimestamp.axis().createFrom(clock, attributes);

            if (tsp == null) {
                return null;
            }

            String variant = attributes.get(Attributes.CALENDAR_VARIANT, "");

            if (variant.isEmpty()) {
                return null;
            }

            StartOfDay startOfDay = attributes.get(Attributes.START_OF_DAY, StartOfDay.EVENING);
            tsp = tsp.minus(startOfDay.getDeviation(tsp.getCalendarDate()), ClockUnit.SECONDS);
            return tsp.getCalendarDate().transform(HijriCalendar.class, variant);

        }

        @Override
        public HijriCalendar createFrom(
            ChronoEntity<?> entity,
            AttributeQuery attributes,
            boolean preparsing
        ) {

            String variant = attributes.get(Attributes.CALENDAR_VARIANT, "");

            if (variant.isEmpty()) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing Hijri calendar variant.");
                return null;
            }

            MonthBasedCalendarSystem<HijriCalendar> calsys = CALSYS.get(variant);

            if (calsys == null) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Unknown Hijri calendar variant: " + variant);
                return null;
            }

            if (!entity.contains(YEAR_OF_ERA)) {
                entity.with(ValidationElement.ERROR_MESSAGE, "Missing islamic year.");
                return null;
            }

            int hyear = entity.get(YEAR_OF_ERA).intValue();

            if (entity.contains(MONTH_OF_YEAR) && entity.contains(DAY_OF_MONTH)) {
                int hmonth = entity.get(MONTH_OF_YEAR).getValue();
                int hdom = entity.get(DAY_OF_MONTH).intValue();
                if (calsys.isValid(HijriEra.ANNO_HEGIRAE, hyear, hmonth, hdom)) {
                    return HijriCalendar.of(variant, hyear, hmonth, hdom);
                } else {
                    entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Hijri date.");
                }
            } else if (entity.contains(DAY_OF_YEAR)) {
                int hdoy = entity.get(DAY_OF_YEAR).intValue();
                if (hdoy > 0) {
                    int hmonth = 1;
                    int daycount = 0;
                    while (hmonth <= 12) {
                        int len = calsys.getLengthOfMonth(HijriEra.ANNO_HEGIRAE, hyear, hmonth);
                        if (hdoy > daycount + len) {
                            hmonth++;
                            daycount += len;
                        } else {
                            int hdom = hdoy - daycount;
                            return HijriCalendar.of(variant, hyear, hmonth, hdom);
                        }
                    }
                }
                entity.with(ValidationElement.ERROR_MESSAGE, "Invalid Hijri date.");
            }

            return null;

        }

        @Override
        public ChronoDisplay preformat(HijriCalendar context, AttributeQuery attributes) {

            return context;

        }

        @Override
        public Chronology<?> preparser() {

            return null;

        }

    }

}