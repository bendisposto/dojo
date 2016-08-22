package tddtrainer.logic;

import tddtrainer.catalog.Exercise;

public interface PhaseManagerIF {

	/**
	 * Checks if the given code in the exercise is valid or not for the current phase
	 * @param exercise The Exercise to check
	 * @param continuePhase if the phase manager should continue to the next phase if the Exercise is valid
	 * @return a PhaseStatus which contains if the Phase is valid and the ExecutionResult
	 */
	public PhaseStatus checkPhase(Exercise exercise, boolean continuePhase);
	
	/**
	 * 
	 * @return returns the current Phase
	 */
	public Phase getPhase();

	public void resetPhase();
	
	public void selectExercise();
	
	public void displayTracking();
	
	public Exercise getOriginalExercise();
	
}
