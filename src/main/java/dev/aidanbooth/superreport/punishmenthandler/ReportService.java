package dev.aidanbooth.superreport.punishmenthandler;

import dev.aidanbooth.superreport.Main;
import dev.aidanbooth.superreport.database.PlayerDataRepository;
import dev.aidanbooth.superreport.database.ReportRepository;
import dev.aidanbooth.superreport.database.model.PlayerData;
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

import static dev.aidanbooth.superreport.utils.ConfigUtils.Colour;

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

        this.successMessage = Colour(plugin.getMessageConfig().config().getString("ReportCommand.ReportSuccessful"));
        this.errorMessage = Colour(plugin.getMessageConfig().config().getString("ReportCommand.ReportError"));
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
        } catch (SQLException e) {
            plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate("001"));
            e.printStackTrace();
            if (reporter != null) reporter.sendMessage(errorMessage);
        }
    }
    public void resolve(PunishmentDraft draft) {
        if (draft.getAction() == PunishmentDraft.Action.PUNISH) {
            plugin.getPunishmentService().punishPlayer(draft.build());
        }

        try {
            // TODO Update to enums?
            reports.archive(draft.getReportId(), draft.getResolver(), draft.getAction().toString());
        } catch (SQLException e) {
            // TODO Handle error
            e.printStackTrace();
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

    private void logError(String code, SQLException e) {
        plugin.getServer().getConsoleSender().sendMessage(ErrorCodes.getTemplate(code));
        e.printStackTrace();
    }
}