package tddtrainer.tracking;

import tddtrainer.catalog.Exercise;
import tddtrainer.logic.PhaseStatus;

public interface TrackingManagerIF {
	
	public void track(Exercise exercise, PhaseStatus phaseStatus);

}
