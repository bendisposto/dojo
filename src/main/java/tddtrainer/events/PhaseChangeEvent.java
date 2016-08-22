package tddtrainer.events;

import tddtrainer.logic.Phase;

public class PhaseChangeEvent {

	private Phase phase;

	public PhaseChangeEvent(Phase phase) {
		this.phase = phase;
	}

	public Phase getPhase() {
		return phase;
	}
}
