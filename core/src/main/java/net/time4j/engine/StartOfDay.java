/*
 * -----------------------------------------------------------------------
 * Copyright © 2013-2015 Meno Hochschild, <http://www.menodata.de/>
 * -----------------------------------------------------------------------
 * This file (StartOfDay.java) is part of project Time4J.
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

package net.time4j.engine;


/**
 * <p>Defines the start of a given calendar day relative to midnight in seconds. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 * @doctags.spec    All subclasses must be immutable.
 */
/*[deutsch]
 * <p>Definiert den Start eines Kalendertages relativ zu Mitternacht in Sekunden. </p>
 *
 * @author  Meno Hochschild
 * @since   3.5/4.3
 * @doctags.spec    All subclasses must be immutable.
 */
public abstract class StartOfDay {

    //~ Statische Felder/Initialisierungen --------------------------------

    /**
     * Default start of calendar day at midnight.
     */
    /*[deutsch]
     * Standardbeginn eines Kalendertages zu Mitternacht.
     */
    public static final StartOfDay MIDNIGHT = new FixedStartOfDay(0);

    /**
     * Start of calendar day at 18:00 on previous day.
     */
    /*[deutsch]
     * Beginn eines Kalendertages zu 18 Uhr am Vortag.
     */
    public static final StartOfDay EVENING = new FixedStartOfDay(-21600);

    //~ Konstruktoren -------------------------------------------------

    /**
     * <p>For immutable subclasss only. </p>
     */
    /*[deutsch]
     * <p>Nur f&uuml;r Subklassen, die <i>immutable</i> sind. </p>
     */
    protected StartOfDay() {
        super();

    }

    //~ Methoden ------------------------------------------------------

    /**
     * <p>Yields the start of given calendar day relative to midnight in seconds. </p>
     *
     * @param   calendarDay     calendar day
     * @return  deviation of start of day relative to midnight in seconds
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Definiert den Start des angegebenen Kalendertages relativ zu Mitternacht in Sekunden. </p>
     *
     * @param   calendarDay     calendar day
     * @return  deviation of start of day relative to midnight in seconds
     * @since   3.5/4.3
     */
    public abstract int getDeviation(Calendrical<?, ?> calendarDay);

    /**
     * <p>Liefert the start of given calendar day relative to midnight in fixed seconds. </p>
     *
     * <p>A negative deviation is explicitly allowed and refers to the previous calendar day. </p>
     *
     * @param   deviation   the deviation of start of day relative to midnight in seconds
     * @return  start of day
     * @since   3.5/4.3
     */
    /*[deutsch]
     * <p>Definiert den Start des angegebenen Kalendertages relativ zu Mitternacht fest in Sekunden. </p>
     *
     * <p>Eine negative Abweichung ist ausdr&uuml;cklich erlaubt und bezieht sich auf den Vortag. </p>
     *
     * @param   deviation   the deviation of start of day relative to midnight in seconds
     * @return  start of day
     * @since   3.5/4.3
     */
    public static StartOfDay ofFixedDeviation(int deviation) {

        if (deviation == 0){
            return MIDNIGHT;
        } else if (deviation == -21600) {
            return EVENING;
        }

        return new FixedStartOfDay(deviation);

    }

    //~ Innere Klassen ----------------------------------------------------

    private static class FixedStartOfDay
        extends StartOfDay {

        //~ Instanzvariablen ------------------------------------------

        private final int deviation;

        //~ Konstruktoren ---------------------------------------------

        private FixedStartOfDay(int deviation) {
            super();

            this.deviation = deviation;

        }

        //~ Methoden --------------------------------------------------

        @Override
        public int getDeviation(Calendrical<?, ?> calendarDay) {

            return this.deviation;

        }

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            } else if (obj instanceof FixedStartOfDay) {
                FixedStartOfDay that = (FixedStartOfDay) obj;
                return (this.deviation == that.deviation);
            } else {
                return false;
            }

        }

        @Override
        public int hashCode() {

            return this.deviation;

        }

        @Override
        public String toString() {

            return "FixedStartOfDay[" + this.deviation + "]";

        }

    }

}