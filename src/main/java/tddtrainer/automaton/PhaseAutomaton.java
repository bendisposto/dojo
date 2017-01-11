package tddtrainer.automaton;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import tddtrainer.compiler.AutoCompiler;
import tddtrainer.compiler.AutoCompilerResult;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.Views;
import tddtrainer.events.automaton.EnforceRefactoringEvent;
import tddtrainer.events.automaton.ProceedPhaseRequest;
import tddtrainer.events.automaton.ResetPhaseEvent;
import tddtrainer.events.automaton.SwitchedToGreenEvent;
import tddtrainer.events.automaton.SwitchedToRedEvent;
import tddtrainer.events.automaton.SwitchedToRefactorEvent;
import tddtrainer.events.babysteps.Babysteps;
import tddtrainer.tracker.ImplementationStep;
import tddtrainer.tracker.Tracker;

public class PhaseAutomaton {

    private Phase currentState = Phase.RED;
    private final EventBus bus;
    private final AutoCompiler autoCompiler;
    private final Tracker tracker;
    private boolean retrospective;

    @Subscribe
    private void init(ExerciseEvent event) {
        autoCompiler.compileAndPost();
        currentState = Phase.RED;
        retrospective = event.getExercise().isRetrospective();
        if (event.getExercise().isBabyStepsActivated())
            bus.post(new Babysteps(180));
        bus.post(new SwitchedToRedEvent());
    }

    @Inject
    public PhaseAutomaton(EventBus bus, AutoCompiler autoCompiler, Tracker tracker) {
        this.bus = bus;
        this.autoCompiler = autoCompiler;
        this.tracker = tracker;
        bus.register(this);
    }

    @Subscribe
    private void next(ProceedPhaseRequest event) {
        AutoCompilerResult compilerResult = autoCompiler.recompile();
        switch (currentState) {
        case RED:
            switchToGreen(compilerResult);
            break;
        case GREEN:
            switchToRefactor(compilerResult);
            break;
        case REFACTOR:
            switchToRetrospect(compilerResult);
            break;
        case RETROSPECT:
            switchToRed();
            break;
        }

        canProceed(compilerResult);
    }

    @Subscribe
    private void canProceed(AutoCompilerResult compilerResult) {
        boolean proceed = false;
        switch (currentState) {
        case RED:
            proceed = compilerResult.aMethodIsMissing()
                    || compilerResult.aSingleTestFails();
            break;
        case GREEN:
            proceed = compilerResult.allClassesCompile() && compilerResult.allTestsGreen();
            break;
        case REFACTOR:
            proceed = compilerResult.allClassesCompile() && compilerResult.allTestsGreen();
            break;
        case RETROSPECT:
            proceed = true;
            break;
        }
        bus.post(new CanProceedEvent(proceed));
    }

    @Subscribe
    private void reset(ResetPhaseEvent event) {
        currentState = Phase.RED;
        bus.post(new SwitchedToRedEvent());
    }

    @Subscribe
    private void enforceRefactor(EnforceRefactoringEvent event) {
        currentState = Phase.REFACTOR;
        bus.post(new SwitchedToRefactorEvent());
    }

    private void switchToGreen(AutoCompilerResult compilerResult) {
        boolean proceed = compilerResult.aMethodIsMissing()
                || compilerResult.aSingleTestFails();
        if (proceed) {
            tracker.addStep(
                    new ImplementationStep(Phase.RED, null, compilerResult.getCodeCU(), compilerResult.getTestCU(),
                            compilerResult.getCompilerOutput(), compilerResult.getTestOutput()));
            currentState = Phase.GREEN;
            bus.post(new SwitchedToGreenEvent());
        }
    }

    private void switchToRefactor(AutoCompilerResult compilerResult) {
        tracker.addStep(
                new ImplementationStep(Phase.GREEN, null, compilerResult.getCodeCU(), compilerResult.getTestCU(),
                        compilerResult.getCompilerOutput(), compilerResult.getTestOutput()));
        boolean proceed = compilerResult.allClassesCompile() && compilerResult.allTestsGreen();
        if (proceed) {
            currentState = Phase.REFACTOR;
            bus.post(new SwitchedToRefactorEvent());
        }
    }

    private void switchToRed() {
        currentState = Phase.RED;
        bus.post(new SwitchedToRedEvent());
    }

    private void switchToRetrospect(AutoCompilerResult compilerResult) {
        tracker.addStep(
                new ImplementationStep(Phase.REFACTOR, null, compilerResult.getCodeCU(), compilerResult.getTestCU(),
                        compilerResult.getCompilerOutput(), compilerResult.getTestOutput()));
        boolean proceed = compilerResult.allClassesCompile() && compilerResult.allTestsGreen();
        if (proceed) {
            if (retrospective) {
                currentState = Phase.RETROSPECT;
                bus.post(Views.RETROSPECT);
            } else {
                currentState = Phase.RED;
                bus.post(new SwitchedToRedEvent());
            }
        }
    }

}
