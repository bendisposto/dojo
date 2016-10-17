package tddtrainer.automaton;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import tddtrainer.compiler.AutoCompiler;
import tddtrainer.compiler.AutoCompilerResult;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.Views;
import tddtrainer.events.automaton.ProceedPhaseRequest;
import tddtrainer.events.automaton.ResetPhaseEvent;
import tddtrainer.events.automaton.SwitchedToGreenEvent;
import tddtrainer.events.automaton.SwitchedToRedEvent;
import tddtrainer.events.automaton.SwitchedToRefactorEvent;
import tddtrainer.events.babysteps.Babysteps;

public class PhaseAutomaton {

    private Phase currentState = Phase.RED;
    private EventBus bus;
    private AutoCompiler autoCompiler;

    @Subscribe
    private void init(ExerciseEvent event) {
        autoCompiler.compileAndPost();
        currentState = Phase.RED;
        if (event.getExercise().isBabyStepsActivated())
            bus.post(new Babysteps(180));
        bus.post(new SwitchedToRedEvent());
    }

    @Inject
    public PhaseAutomaton(EventBus bus, AutoCompiler autoCompiler) {
        this.bus = bus;
        this.autoCompiler = autoCompiler;
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

    private void switchToGreen(AutoCompilerResult compilerResult) {
        boolean proceed = compilerResult.aMethodIsMissing()
                || compilerResult.aSingleTestFails();
        if (proceed) {
            currentState = Phase.GREEN;
            bus.post(new SwitchedToGreenEvent());
        }
    }

    private void switchToRefactor(AutoCompilerResult compilerResult) {
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
        boolean proceed = compilerResult.allClassesCompile() && compilerResult.allTestsGreen();
        if (proceed) {
            currentState = Phase.RETROSPECT;
            bus.post(Views.RETROSPECT);
        }
    }

}
