package dev.aidanbooth.superreport.pipeline.punishPipeline;

import dev.aidanbooth.superreport.database.model.Report;
import dev.aidanbooth.superreport.pipeline.Pipeline;
import dev.aidanbooth.superreport.pipeline.PipelineStep;
import dev.aidanbooth.superreport.pipeline.data.PunishmentDraft;
import dev.aidanbooth.superreport.pipeline.punishPipeline.steps.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PunishmentPipeline extends Pipeline<PunishmentDraft> {
    public PunishmentPipeline(Consumer<PunishmentDraft> onComplete, Supplier<List<Report>> reportSource) {
        super(onComplete,
                new GetReportStep(),
                new ControlPanelStep(),
                new PunishTypeStep(),
                new ReasonStep(),
                new DurationStep(),
                new DismissReportStep());
    }
}
