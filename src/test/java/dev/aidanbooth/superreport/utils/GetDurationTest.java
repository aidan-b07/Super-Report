package dev.aidanbooth.superreport.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetDurationTest {

    @Test
    void permanentAndZero() {
        assertEquals("Permanent", GetDuration.format(-1));
        assertEquals("Permanent", GetDuration.until(null));
        assertEquals("0 seconds", GetDuration.format(0));
    }

    @Test
    void singularAndPlural() {
        assertEquals("1 second", GetDuration.format(1));
        assertEquals("2 minutes", GetDuration.format(120));
        assertEquals("1 hour", GetDuration.format(3600));
    }

    @Test
    void skipsZeroUnits() {
        assertEquals("1 day, 2 hours, 3 minutes", GetDuration.format(93784));
        assertEquals("1 day, 3 minutes", GetDuration.format(86400 + 180));
    }

    @Test
    void matchesTheDurationMenuPresets() {
        assertEquals("1 week", GetDuration.format(604800));
        assertEquals("2 weeks", GetDuration.format(1209600));
        assertEquals("2 months", GetDuration.format(5356800));
        assertEquals("6 months", GetDuration.format(16070400));
        assertEquals("1 year", GetDuration.format(31536000));
    }

    @Test
    void onlyShowsUnitsCloseToTheLargest() {
        assertEquals("1 year", GetDuration.format(31536000 + 5));
        assertEquals("1 day, 2 hours", GetDuration.format(93784, 2));
    }

    @Test
    void agoUsesTheLargestUnit() {
        assertEquals("3 hours ago", GetDuration.ago(System.currentTimeMillis() - 3 * 3600_000L - 5_000));
        assertEquals("just now", GetDuration.ago(System.currentTimeMillis()));
    }

    @Test
    void untilRoundsUp() {
        assertEquals("2 minutes", GetDuration.until(System.currentTimeMillis() + 119_500));
        assertEquals("0 seconds", GetDuration.until(System.currentTimeMillis() - 5000));
    }
}
