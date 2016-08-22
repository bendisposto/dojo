package tddtrainer.tracking;

import javafx.scene.shape.Rectangle;
import tddtrainer.events.ExecutionResultEvent;

public class MyRectangle extends Rectangle {

	Snapshot snapshot;
	private TrackingController controller;
	
    public MyRectangle(double width, double height, Snapshot snapshot) {
    	
       super(width, height, snapshot.getColor());
       this.snapshot = snapshot;
        
        setOnMouseClicked(event -> {
        	controller.editorViewController.showExercise(snapshot.exercise);
        	ExecutionResultEvent eREvent = new ExecutionResultEvent(snapshot.phaseStatus);
        	controller.editorViewController.showExecutionResult(eREvent);
        });
    }

	public void setController(TrackingController trackingController) {
		this.controller = trackingController;
	}
}

