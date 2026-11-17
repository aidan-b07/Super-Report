package dev.aidanbooth.superreport;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateCheckerTest {

    @Test
    void newerNumbersWin() {
        assertTrue(UpdateChecker.isNewer("2.1", "2.0"));
        assertTrue(UpdateChecker.isNewer("2.0.1", "2.0"));
        assertTrue(UpdateChecker.isNewer("2.10", "2.9"));
        assertTrue(UpdateChecker.isNewer("3.0", "2.9.9"));
    }

    @Test
    void sameOrOlderIsNotAnUpdate() {
        assertFalse(UpdateChecker.isNewer("2.0", "2.0"));
        assertFalse(UpdateChecker.isNewer("2.0.0", "2.0"));
        assertFalse(UpdateChecker.isNewer("1.5", "2.0"));
        assertFalse(UpdateChecker.isNewer("1.5", "2.0-SNAPSHOT"));
    }

    @Test
    void aReleaseIsNewerThanItsSnapshot() {
        assertTrue(UpdateChecker.isNewer("2.0", "2.0-SNAPSHOT"));
        assertFalse(UpdateChecker.isNewer("2.0-SNAPSHOT", "2.0"));
        assertFalse(UpdateChecker.isNewer("2.0-SNAPSHOT", "2.0-SNAPSHOT"));
    }

    @Test
    void readsTheTagFromGitHubsReply() {
        String reply = "{\"url\":\"https://api.github.com/repos/x/y/releases/1\",\"tag_name\": \"v2.1\",\"name\":\"Super-Report v2.1\"}";
        assertEquals("2.1", UpdateChecker.tagFrom(reply));
        assertEquals("1.5", UpdateChecker.tagFrom("{\"tag_name\":\"1.5\"}"));
        assertNull(UpdateChecker.tagFrom("{\"message\":\"Not Found\"}"));
    }

    @Test
    void oddVersionsDontBreakIt() {
        assertTrue(UpdateChecker.isNewer("v2.1", "2.0"));
        assertFalse(UpdateChecker.isNewer("not a version", "2.0"));
    }
}
