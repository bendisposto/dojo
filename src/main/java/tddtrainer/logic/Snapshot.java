package tddtrainer.logic;

import tddtrainer.catalog.Exercise;

public class Snapshot {

	private Exercise exercise;
	private PhaseStatus phaseStatus;

	public Snapshot(Exercise exercise, PhaseStatus phaseStatus) {
		this.phaseStatus = phaseStatus;
		this.exercise = new Exercise(exercise);
	}

	public Exercise getExercise() {
		return exercise;
	}

	public PhaseStatus getPhaseStatus() {
		return phaseStatus;
	}

}
