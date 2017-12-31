package tddtrainer.tracker;

import java.time.Duration;

import tddtrainer.automaton.Phase;

public class RetrospectiveStep extends AbstractStep {

    private String retrospecive;
    private String plan;

    public RetrospectiveStep(Duration duration, String retrospecive, String plan) {
        super(Phase.RETROSPECT, duration);
        this.retrospecive = retrospecive;
        this.plan = plan;
    }

    public String getRetrospecive() {
        return retrospecive;
    }

    public String getPlan() {
        return plan;
    }

    @Override
    public String toString() {
        return "RETRO " + retrospecive + " " + plan;
    }

}
