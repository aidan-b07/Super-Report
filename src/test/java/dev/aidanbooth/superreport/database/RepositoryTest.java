package dev.aidanbooth.superreport.database;

import dev.aidanbooth.superreport.database.model.PlayerData;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.database.model.Report;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @TempDir
    File folder;

    private Database database;
    private PlayerDataRepository playerData;
    private ReportRepository reports;
    private PunishmentRepository punishments;

    private final UUID alice = UUID.randomUUID();
    private final UUID bob = UUID.randomUUID();
    private final UUID staff = UUID.randomUUID();

    @BeforeEach
    void setUp() throws SQLException {
        database = Database.sqlite(new File(folder, "test.db"));
        database.connect();
        playerData = new PlayerDataRepository(database);
        reports = new ReportRepository(database);
        punishments = new PunishmentRepository(database);
    }

    @AfterEach
    void tearDown() {
        database.close();
    }

    @Test
    void firstJoinCreatesRowAndLaterJoinsUpdateIp() throws SQLException {
        playerData.recordJoin(alice, "1.1.1.1");
        playerData.recordJoin(alice, "2.2.2.2");

        PlayerData data = playerData.find(alice).orElseThrow(AssertionError::new);
        assertEquals("2.2.2.2", data.ip());
        assertTrue(data.online());
        assertEquals(0, data.reports());
        assertEquals(1, database.query("SELECT * FROM " + Tables.PLAYER_DATA, row -> true).size());
    }

    @Test
    void reportCountersIncrementTheRightPlayer() throws SQLException {
        playerData.recordJoin(alice, "1.1.1.1");
        playerData.recordJoin(bob, "2.2.2.2");

        playerData.incrementReports(alice);
        playerData.incrementReported(bob);

        assertEquals(1, playerData.find(alice).get().reports());
        assertEquals(0, playerData.find(alice).get().reported());
        assertEquals(1, playerData.find(bob).get().reported());
    }

    @Test
    void quittingAndRestartingMarkPlayersOffline() throws SQLException {
        playerData.recordJoin(alice, "1.1.1.1");
        playerData.recordJoin(bob, "2.2.2.2");

        playerData.recordQuit(alice);
        assertFalse(playerData.find(alice).get().online());
        assertTrue(playerData.find(bob).get().online());

        playerData.markAllOffline();
        assertFalse(playerData.find(bob).get().online());
    }

    @Test
    void punishmentsAreCountedPerPlayer() throws SQLException {
        playerData.recordJoin(alice, "1.1.1.1");

        punishments.add(PunishmentType.BAN, alice, "hacking", staff, 600);
        punishments.add(PunishmentType.MUTE, alice, "spam", staff, 600);
        punishments.add(PunishmentType.MUTE, alice, "spam again", staff, PunishmentRepository.PERMANENT);

        PlayerData data = playerData.find(alice).get();
        assertEquals(1, data.bans());
        assertEquals(2, data.mutes());
    }

    @Test
    void reasonsWithQuotesAreStoredAsTyped() throws SQLException {
        String reason = "he's flying'); DROP TABLE " + Tables.REPORTS_ACTIVE + "; --";
        Report report = reports.create(alice, bob, reason);

        assertEquals(reason, reports.find(report.id()).get().reason());
        assertTrue(reports.exists(alice, bob));
        assertFalse(reports.exists(bob, alice));
    }

    @Test
    void archivingAReportMovesItToTheInactiveTable() throws SQLException {
        Report report = reports.create(alice, bob, "spam");

        assertTrue(reports.archive(report.id(), staff, "dismissed"));
        assertFalse(reports.archive(report.id(), staff, "dismissed"));

        assertTrue(reports.findAll().isEmpty());
        List<String> outcomes = database.query(
                "SELECT `PUNISHMENT` FROM " + Tables.REPORTS_INACTIVE + " WHERE `RESOLVER_UUID` = ?",
                row -> row.getString(1), staff.toString());
        assertEquals(1, outcomes.size());
        assertEquals("dismissed", outcomes.get(0));
    }

    @Test
    void temporaryAndPermanentPunishments() throws SQLException {
        punishments.add(PunishmentType.BAN, alice, "hacking", staff, 3600);
        punishments.add(PunishmentType.MUTE, bob, "spam", staff, PunishmentRepository.PERMANENT);

        Punishment ban = punishments.find(PunishmentType.BAN, alice).orElseThrow(AssertionError::new);
        assertFalse(ban.isPermanent());
        assertFalse(ban.isExpired());
        assertEquals(staff, ban.punisher());

        Punishment mute = punishments.find(PunishmentType.MUTE, bob).orElseThrow(AssertionError::new);
        assertTrue(mute.isPermanent());
        assertFalse(mute.isExpired());

        assertFalse(punishments.find(PunishmentType.MUTE, alice).isPresent());
    }

    @Test
    void expiredPunishmentIsReportedAsExpired() throws SQLException {
        punishments.add(PunishmentType.BAN, alice, "hacking", staff, -3600);

        assertTrue(punishments.find(PunishmentType.BAN, alice).get().isExpired());
    }

    @Test
    void liftingAMuteArchivesItAsAMute() throws SQLException {
        punishments.add(PunishmentType.MUTE, alice, "spam", staff, 600);

        assertTrue(punishments.archive(PunishmentType.MUTE, alice));
        assertFalse(punishments.archive(PunishmentType.MUTE, alice));

        assertFalse(punishments.find(PunishmentType.MUTE, alice).isPresent());
        List<String> types = database.query("SELECT `TYPE` FROM " + Tables.PUNISHMENT_ARCHIVED, row -> row.getString(1));
        assertEquals(1, types.size());
        assertEquals("mute", types.get(0));
    }

    @Test
    void newBansStayNewUntilMarkedSeen() throws SQLException {
        punishments.add(PunishmentType.BAN, alice, "hacking", staff, 600);

        assertEquals(1, punishments.findNew(PunishmentType.BAN).size());
        punishments.markSeen(PunishmentType.BAN, alice);
        assertTrue(punishments.findNew(PunishmentType.BAN).isEmpty());
    }

    @Test
    void readsRowsWrittenBySuperReport1() throws SQLException {
        database.update("INSERT INTO " + Tables.PUNISHMENT_BAN
                        + " (`UUID`, `DATE_TIME_LIFTED`, `REASON`, `PUNISHER_UUID`, `NEW`) VALUES (?, ?, ?, ?, ?)",
                alice.toString(), "2000-01-01 00:00:00", "old ban", staff.toString(), "true");
        database.update("INSERT INTO " + Tables.PUNISHMENT_MUTE
                        + " (`UUID`, `DATE_TIME_LIFTED`, `REASON`, `PUNISHER_UUID`, `NEW`) VALUES (?, ?, ?, ?, ?)",
                bob.toString(), "2099-01-01 00:00:00.0", "old mute", staff.toString(), "true");

        assertTrue(punishments.find(PunishmentType.BAN, alice).get().isPermanent());
        Punishment mute = punishments.find(PunishmentType.MUTE, bob).get();
        assertFalse(mute.isPermanent());
        assertFalse(mute.isExpired());
    }
}
