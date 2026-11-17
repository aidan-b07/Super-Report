package dev.aidanbooth.superreport.database;

import dev.aidanbooth.superreport.database.model.PlayerData;
import dev.aidanbooth.superreport.database.model.PlayerStats;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.database.model.ReportStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {

    @TempDir
    File folder;

    private Database database;
    private PlayerRepository players;
    private ReportRepository reports;
    private PunishmentRepository punishments;
    private DiscordMessageRepository messages;

    private final UUID alice = UUID.randomUUID();
    private final UUID bob = UUID.randomUUID();
    private final UUID staff = UUID.randomUUID();

    @BeforeEach
    void setUp() throws SQLException {
        database = Database.sqlite(new File(folder, "test.db"));
        database.connect();
        players = new PlayerRepository(database);
        reports = new ReportRepository(database);
        punishments = new PunishmentRepository(database);
        messages = new DiscordMessageRepository(database);
    }

    @AfterEach
    void tearDown() {
        database.close();
    }

    @Test
    void migrationsRunOnceAndCanRunAgain() throws SQLException {
        database.close();
        database.connect();
        int version = database.queryFirst("SELECT `version` FROM " + Tables.SCHEMA, row -> row.getInt(1)).orElse(-1);
        assertEquals(Migrations.latest(), version);
        assertEquals(1, database.query("SELECT `version` FROM " + Tables.SCHEMA, row -> true).size());
    }

    @Test
    void joinAddsThenUpdatesAPlayer() throws SQLException {
        players.recordJoin(alice, "Alice", "1.2.3.4", "lobby");
        PlayerData first = players.find(alice).get();
        assertTrue(first.online());
        assertEquals("lobby", first.server());

        players.recordQuit(alice);
        players.recordJoin(alice, "AliceRenamed", "5.6.7.8", "survival");
        PlayerData again = players.find(alice).get();
        assertEquals("AliceRenamed", again.name());
        assertEquals("5.6.7.8", again.lastIp());
        assertEquals("survival", again.server());
        assertEquals(first.firstSeen(), again.firstSeen());
    }

    @Test
    void findsPlayersByNameIgnoringCase() throws SQLException {
        players.recordJoin(alice, "Alice", "1.2.3.4", "lobby");
        assertEquals(alice, players.findByName("aLiCe").get().uuid());
        assertFalse(players.findByName("nobody").isPresent());
    }

    @Test
    void markAllOfflineOnlyTouchesThisServer() throws SQLException {
        players.recordJoin(alice, "Alice", null, "lobby");
        players.recordJoin(bob, "Bob", null, "survival");
        players.markAllOffline("lobby");
        assertFalse(players.find(alice).get().online());
        assertTrue(players.find(bob).get().online());
    }

    @Test
    void openReportsAreNewestFirst() throws SQLException {
        Report older = new Report(UUID.randomUUID(), alice, bob, "Hacking", "lobby", 1000, ReportStatus.OPEN, null, null, null);
        Report newer = new Report(UUID.randomUUID(), staff, bob, "Spamming", "lobby", 2000, ReportStatus.OPEN, null, null, null);
        reports.add(older);
        reports.add(newer);

        List<Report> open = reports.findOpen();
        assertEquals(newer.id(), open.get(0).id());
        assertEquals(older.id(), open.get(1).id());
        assertEquals(2, reports.countOpen());
        assertTrue(reports.hasOpen(alice, bob));
        assertFalse(reports.hasOpen(bob, alice));
    }

    @Test
    void resolvingClosesAReportOnce() throws SQLException {
        Report report = Report.open(alice, bob, "Hacking", "lobby");
        reports.add(report);
        UUID punishmentId = UUID.randomUUID();

        assertTrue(reports.resolve(report.id(), ReportStatus.PUNISHED, staff, punishmentId));
        // A second staff member finishing at the same time doesn't overwrite the first
        assertFalse(reports.resolve(report.id(), ReportStatus.DISMISSED, alice, null));

        Report resolved = reports.find(report.id()).get();
        assertEquals(ReportStatus.PUNISHED, resolved.status());
        assertEquals(staff, resolved.resolvedBy());
        assertEquals(punishmentId, resolved.punishmentId());
        assertNotNull(resolved.resolvedAt());
        assertEquals(0, reports.countOpen());
        assertFalse(reports.hasOpen(alice, bob));
        assertEquals(1, reports.findAgainst(bob).size());
    }

    @Test
    void activePunishmentsIgnoreExpiredAndLiftedOnes() throws SQLException {
        long now = System.currentTimeMillis();
        Punishment expired = new Punishment(UUID.randomUUID(), PunishmentType.BAN, bob, "Old", staff, now - 20000,
                now - 10000, null, null, null, null, "lobby");
        punishments.add(expired);
        assertFalse(punishments.findActive(PunishmentType.BAN, bob).isPresent());

        Punishment temporary = Punishment.issue(PunishmentType.BAN, bob, "Hacking", staff, now + 60000, null, "lobby");
        punishments.add(temporary);
        Optional<Punishment> active = punishments.findActive(PunishmentType.BAN, bob);
        assertTrue(active.isPresent());
        assertEquals(temporary.id(), active.get().id());
        assertEquals(now + 60000, active.get().expiresAt());

        // A mute isn't a ban
        assertFalse(punishments.findActive(PunishmentType.MUTE, bob).isPresent());
    }

    @Test
    void permanentPunishmentsStayActive() throws SQLException {
        Punishment permanent = Punishment.issue(PunishmentType.MUTE, bob, "Spamming", staff, null, null, "lobby");
        punishments.add(permanent);
        Punishment found = punishments.findActive(PunishmentType.MUTE, bob).get();
        assertTrue(found.isPermanent());
        assertNull(found.expiresAt());
    }

    @Test
    void liftingKeepsTheHistory() throws SQLException {
        UUID reportId = UUID.randomUUID();
        Punishment ban = Punishment.issue(PunishmentType.BAN, bob, "Hacking", staff, null, reportId, "lobby");
        punishments.add(ban);

        List<Punishment> lifted = punishments.liftActive(PunishmentType.BAN, bob, alice, "Appeal accepted");
        assertEquals(1, lifted.size());
        assertEquals(alice, lifted.get(0).liftedBy());
        assertTrue(lifted.get(0).isLifted());
        assertFalse(punishments.findActive(PunishmentType.BAN, bob).isPresent());
        // Nothing left to lift
        assertTrue(punishments.liftActive(PunishmentType.BAN, bob, alice, null).isEmpty());

        Punishment stored = punishments.find(ban.id()).get();
        assertEquals("Appeal accepted", stored.liftReason());
        assertEquals(reportId, stored.reportId());
        assertEquals(1, punishments.history(bob).size());
    }

    @Test
    void newBansAreNotifiedOnce() throws SQLException {
        Punishment ban = Punishment.issue(PunishmentType.BAN, bob, "Hacking", staff, null, null, "lobby");
        punishments.add(ban);
        assertEquals(1, punishments.findUnnotified(PunishmentType.BAN).size());
        punishments.markNotified(ban.id());
        assertTrue(punishments.findUnnotified(PunishmentType.BAN).isEmpty());
    }

    @Test
    void statsAreCountedFromHistory() throws SQLException {
        reports.add(Report.open(alice, bob, "Hacking", "lobby"));
        reports.add(Report.open(staff, bob, "Spamming", "lobby"));
        punishments.add(Punishment.issue(PunishmentType.BAN, bob, "Hacking", staff, null, null, "lobby"));
        punishments.add(Punishment.issue(PunishmentType.MUTE, bob, "Spamming", staff, null, null, "lobby"));
        punishments.add(Punishment.issue(PunishmentType.MUTE, bob, "Spamming", staff, null, null, "lobby"));

        PlayerStats stats = players.stats(bob);
        assertEquals(2, stats.timesReported());
        assertEquals(0, stats.reportsSent());
        assertEquals(1, stats.bans());
        assertEquals(2, stats.mutes());
        assertEquals(1, players.stats(alice).reportsSent());
    }

    @Test
    void discordMessagesAreRememberedAndForgotten() throws SQLException {
        UUID reportId = UUID.randomUUID();
        messages.save(DiscordMessageRepository.REPORT, reportId, 111L, 222L);
        messages.save(DiscordMessageRepository.REPORT, reportId, 333L, 444L);

        DiscordMessageRepository.Message message = messages.find(DiscordMessageRepository.REPORT, reportId).get();
        assertEquals(333L, message.channelId);
        assertEquals(444L, message.messageId);
        assertFalse(messages.find(DiscordMessageRepository.PUNISHMENT, reportId).isPresent());

        messages.delete(DiscordMessageRepository.REPORT, reportId);
        assertFalse(messages.find(DiscordMessageRepository.REPORT, reportId).isPresent());
    }

    @Test
    void deleteAllEmptiesReportsAndPunishments() throws SQLException {
        reports.add(Report.open(alice, bob, "Hacking", "lobby"));
        punishments.add(Punishment.issue(PunishmentType.BAN, bob, "Hacking", staff, null, null, "lobby"));
        assertEquals(1, reports.deleteAll());
        assertEquals(1, punishments.deleteAll());
        assertEquals(0, reports.countOpen());
    }
}
