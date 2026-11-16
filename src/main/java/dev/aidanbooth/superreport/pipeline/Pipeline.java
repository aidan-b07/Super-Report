package dev.aidanbooth.superreport.pipeline;

import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Pipeline<T> {
    private final List<PipelineStep<T>> steps;
    private final Consumer<T> onComplete;

    @SafeVarargs
    public Pipeline(Consumer<T> onComplete, PipelineStep<T>... steps) {
        if (steps.length == 0) throw new IllegalArgumentException("A pipeline needs at least one step");
        this.steps = Collections.unmodifiableList(Arrays.asList(steps));
        this.onComplete = onComplete;
    }

    public PipelineStep<T> find(Class<?> type) {
        for (PipelineStep<T> step : steps) {
            if (type.isInstance(step)) return step;
        }
        throw new IllegalStateException(type.getSimpleName() + " is not part of this pipeline");
    }

    List<PipelineStep<T>> getSteps() {
        return steps;
    }

    void complete(T data) {
        onComplete.accept(data);
    }
}
