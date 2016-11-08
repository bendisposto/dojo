package tddtrainer.tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Tracker {
    List<AbstractStep> tracked = new ArrayList<>();

    public void addStep(AbstractStep step) {
        tracked.add(step);
    }

    public List<AbstractStep> getRetrospecives() {
        return tracked.stream().filter(e -> e instanceof RetrospectiveStep)
                .collect(Collectors.toList());
    }

    public List<AbstractStep> getSteps() {
        return Collections.unmodifiableList(tracked);
    }

}
