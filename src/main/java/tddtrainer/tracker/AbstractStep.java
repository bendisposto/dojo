package tddtrainer.tracker;

import tddtrainer.automaton.Phase;

import java.time.Duration;

public abstract class AbstractStep {

    private final Phase phase;
    private final Duration duration;

    AbstractStep(Phase phase, Duration duration) {
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
