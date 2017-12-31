package tddtrainer.tracker;

import java.time.Duration;

import tddtrainer.automaton.Phase;

public abstract class AbstractStep {

    private final Phase phase;
    private final Duration duration;

    public AbstractStep(Phase phase, Duration duration) {
        this.phase = phase;
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public Phase getPhase() {
        return phase;
    }

}
