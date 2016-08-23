package tddtrainer.logic;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import tddtrainer.babysteps.BabystepsManager;
import tddtrainer.catalog.Exercise;
import tddtrainer.events.ExecutionResultEvent;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.PhaseResetEvent;
import tddtrainer.events.babysteps.Babysteps;
import tddtrainer.events.babysteps.StartBabysteps;
import tddtrainer.events.babysteps.StopBabysteps;
import tddtrainer.executor.CompilationResult;
import tddtrainer.executor.ExecutionResult;
import tddtrainer.executor.Executor;
import tddtrainer.gui.catalog.ExerciseSelector;
import tddtrainer.tracking.TrackingManager;
import vk.core.api.CompileError;

/**
 * Manages the phases, whether to go on to the next phase or not.
 * 
 * @author Luisa
 *
 */
@Singleton
public class PhaseManager {

	private Phase phase = Phase.RED;
	private Exercise validExercise;
	private TrackingManager trackingManager;
	private EventBus bus;
	private ExerciseSelector exerciseSelector;
	private Exercise originalExercise;
	private PhaseStatus phaseStatus;

	@Inject
	public PhaseManager(TrackingManager trackingManager, ExerciseSelector exerciseSelector, EventBus bus) {
		this.originalExercise = new Exercise();
		this.trackingManager = trackingManager;
		this.bus = bus;
		bus.register(this);
		this.exerciseSelector = exerciseSelector;
		this.phaseStatus = new PhaseStatus(false, null, phase);
	}

	/**
	 * Checks, if the given {@link Exercise} is valid, depending on the current
	 * {@link Phase}, and moves on or not to the next phase. Also manages the
	 * {@link TrackingManager} and the {@link BabystepsManager}
	 * 
	 * @param exercise
	 *            The current {@link Exercise}
	 * @param continuePhase
	 *            whether or not to move on to the next {@link Phase}, if the
	 *            {@link Exercise} is valid
	 * @return The {@link PhaseStatus} after the {@link Executor Execution}
	 */
	public PhaseStatus checkPhase(Exercise exercise, boolean continuePhase) {
		ExecutionResult executionResult = new Executor().execute(exercise);
		phaseStatus.setExecutionResult(executionResult);
		trackingManager.track(exercise, phaseStatus);

		boolean canChange;

		if (phase == Phase.RED) {
			canChange = switchRed(exercise, continuePhase, executionResult);
		} else {
			canChange = switchGreen(exercise, continuePhase, executionResult);
		}
		phaseStatus = new PhaseStatus(canChange, executionResult, phase);

		bus.post(new ExecutionResultEvent(phaseStatus));
		return phaseStatus;
	}

	private boolean switchGreen(Exercise exercise, boolean continuePhase, ExecutionResult executionResult) {
		boolean canChange;
		boolean allGreen = hasNoCompileErrors(executionResult) &&
				hasNoFailingTests(executionResult);
		if (allGreen) {
			if (continuePhase) {
				if (phase == Phase.GREEN) {
					phase = Phase.REFACTOR;
					bus.post(new StopBabysteps());

				} else {
					phase = Phase.RED;
					bus.post(new StartBabysteps(originalExercise.getBabyStepsTestTime()));
				}
			}
			validExercise = exercise;
		}
		canChange = allGreen;
		return canChange;
	}

	private boolean switchRed(Exercise exercise, boolean continuePhase, ExecutionResult executionResult) {
		boolean canChange;
		boolean valid = true;
		if (executionResult.getCompilerResult().hasCompileErrors()) {
			valid = compileErrorsAreAllowed(executionResult);
		} else if (executionResult.getTestResult().getNumberOfFailedTests() != 1) {
			valid = false;
		}
		if (valid) {
			if (continuePhase) {
				phase = Phase.GREEN;
				bus.post(new StartBabysteps(originalExercise.getBabyStepsCodeTime()));
			}
			validExercise = exercise;
		}
		canChange = valid;
		return canChange;
	}

	private boolean hasNoFailingTests(ExecutionResult executionResult) {
		return executionResult.getTestResult().getNumberOfFailedTests() == 0;
	}

	private boolean hasNoCompileErrors(ExecutionResult executionResult) {
		return !(executionResult.getCompilerResult().hasCompileErrors());
	}

	private boolean compileErrorsAreAllowed(ExecutionResult executionResult) {
		for (CompilationResult cr : executionResult.getCompileErrors()) {
			for (CompileError ce : cr.getCompileErrors()) {
				if (!isMissingSymbol(ce)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isMissingSymbol(CompileError ce) {
		return ce.getMessage().contains("cannot find symbol");
	}

	public Phase getPhase() {
		return phase;
	}

	/**
	 * Phase.RED: Phase stays in phase RED Phase.GREEN: Phase returns to phase
	 * RED Phase.REFACTOR: Throws Exception
	 */
	@Subscribe
	public void resetPhase(PhaseResetEvent event) {
		if (phase == Phase.REFACTOR) {
			throw new IllegalStateException("Reset not permitted during Refactor.");
		}
		if (phase == Phase.GREEN) {
			phase = Phase.RED;
		}

		if (validExercise != null) {
			bus.post(new StartBabysteps(originalExercise.getBabyStepsTestTime()));
			bus.post(new ExerciseEvent(validExercise));
		}
	}

	/**
	 * Selects an {@link Exercise} and manages the {@link TrackingManager} and
	 * the {@link BabystepsManager}.
	 */
	public void selectExercise() {
		Exercise exercise = exerciseSelector.selectExercise();
		if (exercise == null) {
			return;
		}
		if (exercise.isBabyStepsActivated()) {
			bus.post(Babysteps.ON);
		} else {
			bus.post(Babysteps.OFF);
		}
		phase = Phase.RED;
		originalExercise = validExercise = exercise;

		bus.post(new ExerciseEvent(validExercise));
		trackingManager.reset();
		bus.post(new StartBabysteps(originalExercise.getBabyStepsTestTime()));
	}

	public void displayTracking() {
		trackingManager.displayInNewWindow();
	}

	public Exercise getOriginalExercise() {
		return originalExercise;
	}
}
