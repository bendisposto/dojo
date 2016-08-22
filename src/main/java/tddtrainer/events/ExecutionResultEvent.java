package tddtrainer.events;

import tddtrainer.logic.PhaseStatus;

public class ExecutionResultEvent {

	private PhaseStatus phaseStatus;
	
	public ExecutionResultEvent(PhaseStatus phaseStatus) {
		this.phaseStatus = phaseStatus;
	}
	
	public PhaseStatus getPhaseStatus() {
		return phaseStatus;
	}
	
}
