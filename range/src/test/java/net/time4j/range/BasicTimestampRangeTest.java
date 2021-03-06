package net.time4j.range;

import net.time4j.ClockUnit;
import net.time4j.PlainTimestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;


@RunWith(JUnit4.class)
public class BasicTimestampRangeTest {

    @Test
    public void containsTemporalInside() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval
                .between(start, end)
                .contains(PlainTimestamp.of(2014, 3, 1, 14, 45)),
            is(true));
    }

    @Test
    public void containsTemporalLeftEdgeClosed() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval
                .between(start, end)
                .contains(start),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeClosed() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(start, end).withClosedEnd().contains(end),
            is(true));
    }

    @Test
    public void containsTemporalRightEdgeOpen() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(start, end).contains(end),
            is(false));
    }

    @Test
    public void containsTemporalOutside() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval
                .between(start, end)
                .contains(PlainTimestamp.of(2014, 5, 14, 0, 0, 1)),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastOutside() {
        assertThat(
            TimestampInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .contains(PlainTimestamp.of(2015, 3, 1, 0, 0)),
            is(false));
    }

    @Test
    public void containsTemporalInfinitePastInside() {
        assertThat(
            TimestampInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .contains(PlainTimestamp.axis().getMinimum()),
            is(true));
    }

    @Test
    public void containsTemporalInfiniteFutureOutside() {
        assertThat(
            TimestampInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .contains(PlainTimestamp.of(2012, 3, 1, 0, 0)),
            is(false));
    }

    @Test
    public void containsTemporalInfiniteFutureInside() {
        assertThat(
            TimestampInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .contains(PlainTimestamp.axis().getMaximum()),
            is(true));
    }

    @Test
    public void isEmptyInfinitePast() {
        assertThat(
            TimestampInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyInfiniteFuture() {
        assertThat(
            TimestampInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicClosed() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(tsp, tsp).withClosedEnd().isEmpty(),
            is(false));
    }

    @Test
    public void isEmptyAtomicOpen() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(tsp, tsp).isEmpty(),
            is(true));
    }

    @Test
    public void isFiniteInfinitePast() {
        assertThat(
            TimestampInterval
                .until(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .isFinite(),
            is(false));
    }

    @Test
    public void isFiniteInfiniteFuture() {
        assertThat(
            TimestampInterval
                .since(PlainTimestamp.of(2014, 5, 14, 0, 0))
                .isFinite(),
            is(false));
    }

    @Test
    public void isFiniteEmpty() {
        PlainTimestamp tsp = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(tsp, tsp).isFinite(), // empty
            is(true));
    }

    @Test
    public void isFinite() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(start, end).isFinite(),
            is(true));
    }

    @Test
    public void getStart() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(start, end).getStart(),
            is(Boundary.of(IntervalEdge.CLOSED, start)));
    }

    @Test
    public void getEnd() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 0, 0);
        assertThat(
            TimestampInterval.between(start, end).getEnd(),
            is(Boundary.of(IntervalEdge.OPEN, end)));
    }

    @Test
    public void getStartAsLocalDateTime() {
        LocalDateTime start = LocalDateTime.parse("2014-02-27T10:03:15");
        assertThat(
            TimestampInterval.since(start).getStartAsLocalDateTime(),
            is(start));
        assertThat(
            TimestampInterval.since(start).getEndAsLocalDateTime(),
            nullValue());
    }

    @Test
    public void getEndAsLocalDateTime() {
        LocalDateTime end = LocalDateTime.parse("2014-05-14T17:45:30");
        assertThat(
            TimestampInterval.until(end).getEndAsLocalDateTime(),
            is(end));
        assertThat(
            TimestampInterval.until(end).getStartAsLocalDateTime(),
            nullValue());
    }

    @Test
    public void testEquals() {
        PlainTimestamp start1 = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end1 = PlainTimestamp.of(2014, 5, 14, 0, 0);
        PlainTimestamp start2 = start1;
        PlainTimestamp end2 = PlainTimestamp.axis().stepBackwards(end1);
        assertThat(
            TimestampInterval.between(start1, end1)
                .equals(
                    TimestampInterval.between(start2, end2)),
            is(false));
        assertThat(
            TimestampInterval.between(start1, end1)
                .equals(
                    TimestampInterval.between(start2, end2).withClosedEnd()),
            is(false));
        assertThat(
            TimestampInterval.between(start1, end1)
                .equals(TimestampInterval.between(start1, end1)),
            is(true));
    }

    @Test
    public void testHashCode() {
        PlainTimestamp start1 = PlainTimestamp.of(2014, 2, 27, 0, 0);
        PlainTimestamp end1 = PlainTimestamp.of(2014, 5, 14, 0, 0);
        PlainTimestamp start2 = start1;
        PlainTimestamp end2 = PlainTimestamp.axis().stepBackwards(end1);
        assertThat(
            TimestampInterval.between(start1, end1).hashCode(),
            not(TimestampInterval.between(start2, end2).hashCode()));
        assertThat(
            TimestampInterval.between(start1, end1).hashCode(),
            not(
                TimestampInterval.between(start2, end2)
                .withClosedEnd().hashCode()));
        assertThat(
            TimestampInterval.between(start1, end1).hashCode(),
            is(TimestampInterval.between(start1, end1).hashCode()));
    }

    @Test
    public void testToStringInfinitePast() {
        assertThat(
            TimestampInterval
                .until(PlainTimestamp.of(2014, 5, 14, 14, 45))
                .toString(),
            is("(-∞/2014-05-14T14:45)"));
    }

    @Test
    public void testToStringInfiniteFuture() {
        assertThat(
            TimestampInterval
                .since(PlainTimestamp.of(2014, 5, 14, 14, 45))
                .toString(),
            is("[2014-05-14T14:45/+∞)"));
    }

    @Test
    public void testToStringFiniteClosed() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 14, 45);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 9, 30);
        assertThat(
            TimestampInterval.between(start, end).withClosedEnd().toString(),
            is("[2014-02-27T14:45/2014-05-14T09:30]"));
    }

    @Test
    public void testToStringFiniteHalfOpen() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 14, 45);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 9, 30);
        assertThat(
            TimestampInterval
                .between(start, end)
                .toString(),
            is("[2014-02-27T14:45/2014-05-14T09:30)"));
    }

    @Test
    public void withClosedEndNormal() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 14, 45);
        PlainTimestamp end = PlainTimestamp.of(2014, 5, 14, 9, 30);

        TimestampInterval interval = TimestampInterval.between(start, end);
        assertThat(
            interval.withClosedEnd().getEnd().isClosed(),
            is(true));
    }

    @Test(expected=IllegalStateException.class)
    public void withClosedEndInfinite() {
        PlainTimestamp start = PlainTimestamp.of(2014, 2, 27, 14, 45);
        TimestampInterval.since(start).withClosedEnd();
    }

    @Test
    public void move() {
        PlainTimestamp start1 = PlainTimestamp.of(2014, 5, 1, 23, 0);
        PlainTimestamp end1 = PlainTimestamp.of(2014, 5, 2, 16, 0);
        TimestampInterval interval = TimestampInterval.between(start1, end1);

        PlainTimestamp start2 = PlainTimestamp.of(2014, 5, 2, 11, 0);
        PlainTimestamp end2 = PlainTimestamp.of(2014, 5, 3, 4, 0);
        TimestampInterval expected = TimestampInterval.between(start2, end2);

        assertThat(
            interval.move(12, ClockUnit.HOURS),
            is(expected));
    }

}