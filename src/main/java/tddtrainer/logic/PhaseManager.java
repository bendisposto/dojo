package tddtrainer.logic;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import tddtrainer.babysteps.BabystepsManager;
import tddtrainer.catalog.Exercise;
import tddtrainer.events.ExecutionResultEvent;
import tddtrainer.events.ExerciseEvent;
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
public class PhaseManager implements PhaseManagerIF {

	private Phase phase = Phase.RED;
	private Exercise validExercise;
	private TrackingManager trackingManager;
	private EventBus bus;
	private ExerciseSelector exerciseSelector;
	private BabystepsManager babystepsManager;
	private Exercise originalExercise;
	private PhaseStatus phaseStatus;

	@Inject
	public PhaseManager(TrackingManager trackingManager, ExerciseSelector exerciseSelector, EventBus bus) {
		this.originalExercise = new Exercise();
		this.trackingManager = trackingManager;
		this.bus = bus;
		this.exerciseSelector = exerciseSelector;
		this.babystepsManager = new BabystepsManager(this, bus);
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
	@Override
	public PhaseStatus checkPhase(Exercise exercise, boolean continuePhase) {
		ExecutionResult executionResult = new Executor().execute(exercise);
		phaseStatus.setExecutionResult(executionResult);
		trackingManager.track(exercise, phaseStatus);
		if (phase == Phase.RED) {
			boolean valid = true;
			if (executionResult.getCompilerResult().hasCompileErrors()) {
				loop: for (CompilationResult cr : executionResult.getCompileErrors()) {
					for (CompileError ce : cr.getCompileErrors()) {
						if (!(ce.getMessage().contains("cannot find symbol") || ce.getMessage().contains("method"))) {
							valid = false;
							break loop;
						}
					}
				}
			} else if (executionResult.getTestResult().getNumberOfFailedTests() != 1) {
				valid = false;
			}
			if (valid) {
				if (continuePhase) {
					phase = Phase.GREEN;
					babystepsManager.start(originalExercise.getBabyStepsCodeTime());
				}
				validExercise = exercise;
			}
			phaseStatus = new PhaseStatus(valid, executionResult, phase);
		}

		else {
			if ((!(executionResult.getCompilerResult().hasCompileErrors())) &&
					(executionResult.getTestResult().getNumberOfFailedTests() == 0)) {
				if (continuePhase) {
					if (phase == Phase.GREEN) {
						phase = Phase.REFACTOR;
						babystepsManager.stop();
					} else {
						phase = Phase.RED;
						babystepsManager.start(originalExercise.getBabyStepsTestTime());
					}
				}
				validExercise = exercise;
				phaseStatus = new PhaseStatus(true, executionResult, phase);
			} else {
				phaseStatus = new PhaseStatus(false, executionResult, phase);
			}
		}
		bus.post(new ExecutionResultEvent(phaseStatus));
		return phaseStatus;
	}

	@Override
	public Phase getPhase() {
		return phase;
	}

	/**
	 * Phase.RED: Phase stays in phase RED Phase.GREEN: Phase returns to phase
	 * RED Phase.REFACTOR: Throws Exception
	 */
	@Override
	public void resetPhase() {
		if (phase == Phase.REFACTOR) {
			throw new IllegalStateException("Reset not permitted during Refactor.");
		}
		if (phase == Phase.GREEN) {
			phase = Phase.RED;
		}

		if (validExercise != null) {
			babystepsManager.start(originalExercise.getBabyStepsTestTime());
			bus.post(new ExerciseEvent(validExercise));
		}
	}

	/**
	 * Selects an {@link Exercise} and manages the {@link TrackingManager} and
	 * the {@link BabystepsManager}.
	 */
	@Override
	public void selectExercise() {
		Exercise exercise = exerciseSelector.selectExercise();
		if (exercise == null) {
			return;
		}
		if (exercise.isBabyStepsActivated()) {
			babystepsManager.enable();
		} else {
			babystepsManager.disable();
		}
		phase = Phase.RED;
		originalExercise = validExercise = exercise;

		bus.post(new ExerciseEvent(validExercise));
		trackingManager.reset();
		babystepsManager.start(originalExercise.getBabyStepsTestTime());
	}

	@Override
	public void displayTracking() {
		trackingManager.displayInNewWindow();
	}

	@Override
	public Exercise getOriginalExercise() {
		return originalExercise;
	}
}
