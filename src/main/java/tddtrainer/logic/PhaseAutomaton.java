package tddtrainer.logic;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import tddtrainer.compiler.AutoCompiler;
import tddtrainer.compiler.AutoCompilerResult;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.PhaseResetEvent;
import tddtrainer.logic.events.NextPhaseEvent;
import tddtrainer.logic.events.SwitchToGreenEvent;
import tddtrainer.logic.events.SwitchToRedEvent;
import tddtrainer.logic.events.SwitchToRefactorEvent;

public class PhaseAutomaton {

	private Phase currentState = Phase.RED;
	private EventBus bus;
	private AutoCompiler autoCompiler;

	@Subscribe
	private void init(ExerciseEvent event) {
		autoCompiler.compileAndPost();
		currentState = Phase.RED;
		bus.post(new SwitchToRedEvent());
	}

	@Inject
	public PhaseAutomaton(EventBus bus, AutoCompiler autoCompiler) {
		this.bus = bus;
		this.autoCompiler = autoCompiler;
		bus.register(this);
	}

	@Subscribe
	private void next(NextPhaseEvent event) {
		AutoCompilerResult compilerResult = autoCompiler.recompile();
		switch (currentState) {
		case RED:
			switchToGreen(compilerResult);
			break;
		case GREEN:
			switchToRefactor(compilerResult);
			break;
		case REFACTOR:
			switchToRed(compilerResult);
			break;
		}
	}

	@Subscribe
	private void reset(PhaseResetEvent event) {
		currentState = Phase.RED;
		bus.post(new SwitchToRedEvent());
	}

	private void switchToGreen(AutoCompilerResult compilerResult) {
		boolean proceed = compilerResult.aMethodIsMissing()
				|| compilerResult.aSingleTestFails();
		if (proceed) {
			currentState = Phase.GREEN;
			bus.post(new SwitchToGreenEvent());
		}
		bus.post(new ProceedPhaseEvent(proceed));
	}

	private void switchToRefactor(AutoCompilerResult compilerResult) {
		boolean proceed = compilerResult.allClassesCompile() && compilerResult.allTestsGreen();
		if (proceed) {
			currentState = Phase.REFACTOR;
			bus.post(new SwitchToRefactorEvent());
		}
		bus.post(new ProceedPhaseEvent(proceed));
	}

	private void switchToRed(AutoCompilerResult compilerResult) {
		boolean proceed = compilerResult.allClassesCompile() && compilerResult.allTestsGreen();
		if (proceed) {
			currentState = Phase.RED;
			bus.post(new SwitchToRedEvent());
		}
		bus.post(new ProceedPhaseEvent(proceed));
	}

}
