package dev.aidanbooth.superreport.pipeline;

public class PipelineStepResult {
    enum Action { STAY, REFRESH, NEXT, BACK, GOTO, COMPLETE, CANCEL }

    public static final PipelineStepResult STAY = new PipelineStepResult(Action.STAY, null);
    public static final PipelineStepResult REFRESH = new PipelineStepResult(Action.REFRESH, null);
    public static final PipelineStepResult NEXT = new PipelineStepResult(Action.NEXT, null);
    public static final PipelineStepResult BACK = new PipelineStepResult(Action.BACK, null);
    public static final PipelineStepResult COMPLETE = new PipelineStepResult(Action.COMPLETE, null);
    public static final PipelineStepResult CANCEL = new PipelineStepResult(Action.CANCEL, null);

    // Jump to another step in the same pipeline; BACK returns here
    public static PipelineStepResult goTo(Class<? extends PipelineStep<?>> step) {
        return new PipelineStepResult(Action.GOTO, step);
    }

    private final Action action;
    private final Class<? extends PipelineStep<?>> target;

    private PipelineStepResult(Action action, Class<? extends PipelineStep<?>> target) {
        this.action = action;
        this.target = target;
    }

    Action action() { return action; }
    Class<? extends PipelineStep<?>> target() { return target; }
}
