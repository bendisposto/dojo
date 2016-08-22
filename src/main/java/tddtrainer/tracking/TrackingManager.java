package tddtrainer.tracking;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.google.common.eventbus.Subscribe;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tddtrainer.Main;
import tddtrainer.catalog.Exercise;
import tddtrainer.events.LanguageChangeEvent;
import tddtrainer.logic.PhaseStatus;

public class TrackingManager implements TrackingManagerIF {

	ArrayList<Snapshot> progress;
	LocalDateTime start;
	ResourceBundle bundle;

	public TrackingManager() {
		progress = new ArrayList<>();
		start = LocalDateTime.now();
	}

	@Override
	public void track(Exercise exercise, PhaseStatus phaseStatus) {
		Snapshot snapshot = new Snapshot(exercise, LocalDateTime.now(), phaseStatus);
		progress.add(snapshot);
	}

	static long getTimeBetweenSnaps(LocalDateTime start, LocalDateTime end) {
		return start.until(end, ChronoUnit.SECONDS);
	}

	public void displayInNewWindow() {
		
		Stage stage = new Stage();
		stage.setMinWidth(800);
		stage.setWidth(800);
		stage.setMinHeight(600);
		stage.setHeight(500);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Tracked Progress");
		
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setResources(bundle);
			loader.setLocation(Main.class.getResource("gui/tracking/Tracking.fxml"));
			BorderPane boarderPane = (BorderPane) loader.load();
			
			TrackingController controller = loader.getController();
			
			controller.setStage(stage);
			controller.generateTrackline(this);
			
			Scene scene = new Scene(boarderPane);
	        stage.setScene(scene);
	        stage.showAndWait();
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void reset() {
		progress = new ArrayList<>();
		start = LocalDateTime.now();
	}
	
	@Subscribe
	public void setResourceBundle(LanguageChangeEvent event) {
		this.bundle = event.getBundle();
	}
}
