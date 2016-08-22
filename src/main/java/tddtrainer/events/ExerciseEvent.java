package tddtrainer.events;

import tddtrainer.catalog.Exercise;

public class ExerciseEvent {
	
	private Exercise exercise;

	public ExerciseEvent(Exercise exercise) {
		this.exercise = exercise;
	}

	public Exercise getExercise() {
		return exercise;
	}
}
