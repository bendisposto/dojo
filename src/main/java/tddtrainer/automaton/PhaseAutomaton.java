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

/**
 * This class handles the phases of the TDDT
 */
public class PhaseAutomaton {

    private Phase currentState = Phase.RED;
    private final EventBus bus;
    private final AutoCompiler autoCompiler;
    private final Tracker tracker;
    private boolean retrospective;

    /**
     * This method initialises the Exercise when the {@ExerciseEvent} is triggered.
     * @param event {@link ExerciseEvent}
     */
    @Subscribe
    private void init(ExerciseEvent event) {
        autoCompiler.compileAndPost();
        currentState = Phase.RED;
        retrospective = event.getExercise().isRetrospective();
        if (event.getExercise().isBabyStepsActivated()) {
            bus.post(new Babysteps(180));
        }
        bus.post(new SwitchedToRedEvent());
    }

    @Inject
    public PhaseAutomaton(EventBus bus, AutoCompiler autoCompiler, Tracker tracker) {
        this.bus = bus;
        this.autoCompiler = autoCompiler;
        this.tracker = tracker;
        bus.register(this);
    }

    /**
     * The order of the {@link Phase}s is implemented in here. For this, a {@link ProceedPhaseRequest} needs to be triggered.
     * @param event {@link ProceedPhaseRequest} when this Event is triggered,
     */
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

    /**
     * This method is used, when the {@link AutoCompilerResult} is available to be processed.
     * The compilerResult is used to check, if the user can proceed from the current state to the next one.
     * @param compilerResult {@link AutoCompilerResult}
     */
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

    /**
     * This method resets the red phase, if the time is up or the user chooses to do so.
     * @param event {@link ResetPhaseEvent}
     */
    @Subscribe
    private void reset(ResetPhaseEvent event) {
        currentState = Phase.RED;
        bus.post(new SwitchedToRedEvent());
    }

    /**
     * This method resets the refactor phase, if the time is up or the user chooses to do so.
     * @param event {@link EnforceRefactoringEvent}
     */
    @Subscribe
    private void enforceRefactor(EnforceRefactoringEvent event) {
        currentState = Phase.REFACTOR;
        bus.post(new SwitchedToRefactorEvent());
    }

    /**
     * This method is called, when the state can be switched to GREEN.
     * It is checked that one can proceed and after that the {@link SwitchedToGreenEvent} is posted to the {@link EventBus}.
     * @param compilerResult {@link AutoCompilerResult} is used to check if the phase can be changed.
     */
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

    /**
     * This method is called, when the state can be switched to REFACTOR.
     * It is checked that one can proceed and after that the {@link SwitchedToRefactorEvent} is posted to the {@link EventBus}.
     * @param compilerResult {@link AutoCompilerResult} is used to check if the phase can be changed.
     */
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

    /**
     * This method switches the phase RED
     */
    private void switchToRed() {
        currentState = Phase.RED;
        bus.post(new SwitchedToRedEvent());
    }

    /**
     * This method is called, when the state can be switched to RETROSPECT.
     * It is checked that one can proceed and after that the {@link SwitchedToRedEvent} is posted to the {@link EventBus}.
     * @param compilerResult
     */
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
