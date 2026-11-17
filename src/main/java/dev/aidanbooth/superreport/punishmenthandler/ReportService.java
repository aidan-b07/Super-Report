package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.PlayerDataRepository;
import dev.aidanbooth.superreport.database.ReportRepository;
import dev.aidanbooth.superreport.database.model.PlayerData;
import dev.aidanbooth.superreport.database.model.PunishmentType;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ReportService {
    private final Main plugin;
    private final ReportRepository reports;
    private final PlayerDataRepository playerData;
    private final Pipeline<ReportDraft> reportPipeline;
    private final Pipeline<PunishmentDraft> punishmentPipeline;

    private final String successMessage;
    private final String errorMessage;

    public enum ReportCheck { ALLOWED, NOT_ONLINE, SELF_REPORT, ALREADY_REPORTED, ERROR }

    public ReportService(Main plugin, ReportRepository reports, PlayerDataRepository playerData) {
        this.plugin = plugin;
        this.reports = reports;
        this.playerData = playerData;

        this.reportPipeline = new Pipeline<>(this::submit, new SelectReportReasonStep());
        this.punishmentPipeline = new PunishmentPipeline(this::resolve, this::getReports);

        this.successMessage = plugin.getMessageConfig().get("ReportCommand.ReportSuccessful");
        this.errorMessage = plugin.getMessageConfig().get("ReportCommand.ReportError");
    }

    public void openReportMenu(Player reporter, OfflinePlayer reported) {
        ReportDraft draft = new ReportDraft(reported.getUniqueId(), reporter.getUniqueId());
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
            Report report = reports.create(draft.getReporter(), draft.getReported(), draft.getReason());
            playerData.incrementReports(draft.getReporter());
            playerData.incrementReported(draft.getReported());
            plugin.getDiscordBot().sendReportEmbed(report);
            if (reporter != null) reporter.sendMessage(successMessage);
            notifyStaff(report);
        } catch (SQLException e) {
            plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("001"));
            e.printStackTrace();
            if (reporter != null) reporter.sendMessage(errorMessage);
        }
    }
    public void resolve(PunishmentDraft draft) {
        Player resolver = Bukkit.getPlayer(draft.getResolver());
        boolean punish = draft.getAction() == PunishmentDraft.Action.PUNISH;
        if (punish) {
            PunishmentService.PunishResult result = plugin.getPunishmentService().punishPlayer(draft.build());
            if (result != PunishmentService.PunishResult.APPLIED) {
                // Nothing was applied, so leave the report open for someone to deal with
                if (resolver != null) resolver.sendMessage(punishFailedMessage(result, draft));
                return;
            }
        }

        String reported = PlayerUtils.GetUsername(draft.getReported());
        try {
            // TODO Update to enums?
            reports.archive(draft.getReportId(), draft.getResolver(), draft.getAction().toString());
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

    private String punishFailedMessage(PunishmentService.PunishResult result, PunishmentDraft draft) {
        String path = result == PunishmentService.PunishResult.BYPASSED ? "Punishment.Bypassed" : "Punishment.Error";
        return plugin.getMessageConfig().get(path)
                .replace("{player}", PlayerUtils.GetUsername(draft.getReported()))
                .replace("{action}", draft.getPunishmentType() == PunishmentType.BAN ? "banned" : "muted");
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

    public ReportCheck canReport(Player reporter, OfflinePlayer reported) {
        if (reported == null) return ReportCheck.NOT_ONLINE;
        if (reporter.getUniqueId().equals(reported.getUniqueId())) return ReportCheck.SELF_REPORT;

        try {
            if (reports.exists(reporter.getUniqueId(), reported.getUniqueId())) return ReportCheck.ALREADY_REPORTED;
        } catch (SQLException e) {
            logError("004", e);
            return ReportCheck.ERROR;
        }
        try {
            // Checked in the database so players on other servers sharing it count as online
            boolean online = playerData.find(reported.getUniqueId()).map(PlayerData::online).orElse(false);
            if (!online) return ReportCheck.NOT_ONLINE;
        } catch (SQLException e) {
            logError("005", e);
            return ReportCheck.ERROR;
        }
        return ReportCheck.ALLOWED;
    }

    public List<Report> getReports() {
        try {
            return reports.findAll();
        } catch (SQLException e) {
            logError("008", e);
            return new ArrayList<>();
        }
    }

    // Debug only: removes every report, active and archived
    public int clearAll() throws SQLException {
        return reports.deleteAll();
    }

    private void logError(String code, SQLException e) {
        plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate(code));
        e.printStackTrace();
    }
}