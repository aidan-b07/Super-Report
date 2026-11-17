package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.PlayerRepository;
import dev.aidanbooth.superreport.database.ReportRepository;
import dev.aidanbooth.superreport.database.model.PlayerData;
import dev.aidanbooth.superreport.database.model.Punishment;
import dev.aidanbooth.superreport.database.model.PunishmentType;
import dev.aidanbooth.superreport.database.model.ReportStatus;
import dev.aidanbooth.superreport.utils.GetDuration;
import dev.aidanbooth.superreport.utils.PlayerUtils;
import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.pipeline.Pipeline;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.data.ReportDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.PunishmentPipeline;
import dev.aidanbooth.superreport.pipeline.reportPipeline.SelectReportReasonStep;
import dev.aidanbooth.superreport.utils.ErrorCodes;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class ReportService {
    private final Main plugin;
    private final ReportRepository reports;
    private final PlayerRepository players;
    private final Pipeline<ReportDraft> reportPipeline;
    private final Pipeline<PunishmentDraft> punishmentPipeline;


    public enum ReportCheck { ALLOWED, NOT_ONLINE, SELF_REPORT, ALREADY_REPORTED, ERROR }

    public ReportService(Main plugin, ReportRepository reports, PlayerRepository players) {
        this.plugin = plugin;
        this.reports = reports;
        this.players = players;

        this.reportPipeline = new Pipeline<>(this::submit, new SelectReportReasonStep());
        this.punishmentPipeline = new PunishmentPipeline(this::resolve);

    }

    public void openReportMenu(Player reporter, UUID reported, String reportedName) {
        ReportDraft draft = new ReportDraft(reported, reporter.getUniqueId());
        draft.setReportedName(reportedName);
        plugin.getPipelineManager().start(reporter, reportPipeline, draft);
    }
    public void openReportsMenu(Player player) {
        PunishmentDraft draft = new PunishmentDraft(player.getUniqueId());
        draft.setShownReports(getReports());
        plugin.getPipelineManager().start(player, punishmentPipeline, draft);
    }

    public void submit(ReportDraft draft) {
        Player reporter = Bukkit.getPlayer(draft.getReporter());
        try {
            Report report = Report.open(draft.getReporter(), draft.getReported(), draft.getReason(), plugin.getServerName());
            reports.add(report);
            plugin.getDiscordBot().sendReportEmbed(report);
            if (reporter != null) reporter.sendMessage(plugin.getMessageConfig().get("ReportCommand.ReportSuccessful"));
            notifyStaff(report);
        } catch (SQLException e) {
            plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("001"));
            e.printStackTrace();
            if (reporter != null) reporter.sendMessage(plugin.getMessageConfig().get("ReportCommand.ReportError"));
        }
    }
    public void resolve(PunishmentDraft draft) {
        Player resolver = Bukkit.getPlayer(draft.getResolver());
        boolean punish = draft.getAction() == PunishmentDraft.Action.PUNISH;
        String reported = PlayerUtils.GetUsername(draft.getReported());

        // Someone else may have dealt with it while this menu was open
        if (!isStillOpen(draft)) {
            if (resolver != null) resolver.sendMessage(plugin.getMessageConfig().get("Report.AlreadyClosed").replace("{player}", reported));
            return;
        }

        Punishment punishment = null;
        if (punish) {
            punishment = draft.build(plugin.getServerName());
            PunishmentService.PunishResult result = plugin.getPunishmentService().punishPlayer(punishment);
            if (result != PunishmentService.PunishResult.APPLIED) {
                // Nothing was applied, so leave the report open for someone to deal with
                if (resolver != null) resolver.sendMessage(punishFailedMessage(result, draft));
                return;
            }
        }

        ReportStatus status = punish ? ReportStatus.PUNISHED : ReportStatus.DISMISSED;
        try {
            if (reports.resolve(draft.getReportId(), status, draft.getResolver(), punishment == null ? null : punishment.id())) {
                plugin.getDiscordBot().markReportResolved(draft.getReportId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (resolver != null) resolver.sendMessage(plugin.getMessageConfig().get("Report.ArchiveError").replace("{player}", reported));
            // The punishment itself was still applied, so carry on and confirm it
        }
        if (resolver == null) return;

        if (punish) {
            int duration = draft.getDuration();
            resolver.sendMessage(plugin.getMessageConfig().get("Punishment.Applied")
                    .replace("{player}", reported)
                    .replace("{action}", draft.getPunishmentType() == PunishmentType.BAN ? "banned" : "muted")
                    .replace("{duration}", duration == GetDuration.PERMANENT ? "permanently" : "for " + GetDuration.format(duration))
                    .replace("{reason}", draft.getReason()));
        } else {
            resolver.sendMessage(plugin.getMessageConfig().get("Report.Dismissed").replace("{player}", reported));
        }
    }

    private boolean isStillOpen(PunishmentDraft draft) {
        try {
            Optional<Report> report = reports.find(draft.getReportId());
            return report.isPresent() && report.get().status() == ReportStatus.OPEN;
        } catch (SQLException e) {
            logError("008", e);
            return false;
        }
    }

    private String punishFailedMessage(PunishmentService.PunishResult result, PunishmentDraft draft) {
        String path = result == PunishmentService.PunishResult.BYPASSED ? "Punishment.Bypassed" : "Punishment.Error";
        return plugin.getMessageConfig().get(path)
                .replace("{player}", PlayerUtils.GetUsername(draft.getReported()))
                .replace("{action}", draft.getPunishmentType() == PunishmentType.BAN ? "banned" : "muted");
    }

    // Tells a staff member how many reports are waiting. The count is read off the main thread
    public void notifyOpenReports(Player staff) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int open;
            try {
                open = reports.countOpen();
            } catch (SQLException e) {
                logError("008", e);
                return;
            }
            if (open == 0) return;
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!staff.isOnline()) return;
                staff.sendMessage(plugin.getMessageConfig().get("ReportCommand.JoinNotification").replace("{count}", String.valueOf(open)));
            });
        });
    }

    // Tells online staff with superreport.notifications, except the reporter who already got their own message
    private void notifyStaff(Report report) {
        String message = plugin.getMessageConfig().get("ReportCommand.StaffNotification")
                .replace("{reporter}", PlayerUtils.GetUsername(report.reporter()))
                .replace("{reported}", PlayerUtils.GetUsername(report.player()))
                .replace("{reason}", report.reason());
        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.getUniqueId().equals(report.reporter())) continue;
            if (staff.hasPermission("superreport.notifications")) staff.sendMessage(message);
        }
    }

    // A player's UUID and name from what was typed: online here first, then anyone on a server sharing the database.
    // Empty for names never seen, without asking Mojang, as only online players can be reported anyway
    public Optional<PlayerData> findReportable(String name) {
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) {
            return Optional.of(new PlayerData(online.getUniqueId(), online.getName(), 0, 0, true, plugin.getServerName(), null));
        }
        try {
            return players.findByName(name);
        } catch (SQLException e) {
            logError("005", e);
            return Optional.empty();
        }
    }

    public ReportCheck canReport(Player reporter, UUID reported) {
        if (reporter.getUniqueId().equals(reported)) return ReportCheck.SELF_REPORT;

        try {
            if (reports.hasOpen(reporter.getUniqueId(), reported)) return ReportCheck.ALREADY_REPORTED;
        } catch (SQLException e) {
            logError("004", e);
            return ReportCheck.ERROR;
        }
        try {
            // Online here, or on another server sharing the database
            boolean online = Bukkit.getPlayer(reported) != null || players.find(reported).map(PlayerData::online).orElse(false);
            if (!online) return ReportCheck.NOT_ONLINE;
        } catch (SQLException e) {
            logError("005", e);
            return ReportCheck.ERROR;
        }
        return ReportCheck.ALLOWED;
    }

    public List<Report> getReports() {
        try {
            return reports.findOpen();
        } catch (SQLException e) {
            logError("008", e);
            return new ArrayList<>();
        }
    }

    // Debug only: removes every report
    public int clearAll() throws SQLException {
        return reports.deleteAll();
    }

    private void logError(String code, SQLException e) {
        plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate(code));
        e.printStackTrace();
    }
}