package tddtrainer.tracking;

import java.time.LocalDateTime;

import javafx.scene.paint.Color;
import tddtrainer.catalog.Exercise;
import tddtrainer.logic.PhaseStatus;

public class Snapshot {

	Exercise exercise;
	LocalDateTime pointOfTime;
	PhaseStatus phaseStatus;

	public Snapshot(Exercise exercise, LocalDateTime pointOfTime, PhaseStatus phaseStatus) {
		this.exercise = exercise; 
		this.pointOfTime = pointOfTime;
		this.phaseStatus = phaseStatus;
	}
	
	Color getColor(){
	    	switch(phaseStatus.getPhase()) {
	    	case RED:
	    		return Color.CRIMSON;
	    	case GREEN:
	    		return Color.FORESTGREEN;
	    	default:
	    		return Color.web("#6f8391");
	    		
	    	}
	}
}
