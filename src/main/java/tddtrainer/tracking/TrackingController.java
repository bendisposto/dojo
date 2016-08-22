package tddtrainer.tracking;


import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tddtrainer.Main;
import tddtrainer.gui.EditorViewController;



public class TrackingController {

	private Stage stage;
	private TrackingManager trackingManager;
	EditorViewController editorViewController;
	
	@FXML
    private BorderPane borderPane;
    
	@FXML
    private AnchorPane centerPane;
	
    @FXML
    private Label labelHeader;
    
    @FXML
    private Button buttonClose;
    
    @FXML
    ListView<String> listViewCompilationResult;
    
    @FXML
    ListView<String> listViewTestResult;

    @FXML
    private HBox hboxTracking;
    
	private void showEditorView() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("gui/EditorView.fxml"));
			loader.setResources(trackingManager.bundle);
			SplitPane editorView = loader.load();
			editorViewController = loader.getController();
			centerPane.getChildren().add(editorView);
			AnchorPane.setBottomAnchor(editorView, 0.0);
			AnchorPane.setLeftAnchor(editorView, 5.0);
			AnchorPane.setRightAnchor(editorView, 5.0);
			AnchorPane.setTopAnchor(editorView, 0.0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    
	void generateTrackline(TrackingManager trackingManager) {
		
		this.trackingManager = trackingManager;
		showEditorView();
		
		try {
			labelHeader.setText(trackingManager.bundle.getString("tracking.header2") + "'" + trackingManager.progress.get(0).exercise.getName() + "'");
			
		} catch (Exception e) {
		}
		
		generateRectangles();
		buttonClose.setOnAction(event -> stage.close());
		
	}
	
	
	private void generateRectangles() {
		
		int size = trackingManager.progress.size();
		long timeOfSnap;
		
		for (int i = 0; i<size; i++) {
			timeOfSnap = 0;
			if(i == 0) {
				timeOfSnap =  TrackingManager.getTimeBetweenSnaps(trackingManager.start, trackingManager.progress.get(0).pointOfTime);
			} else { 
				timeOfSnap = TrackingManager.getTimeBetweenSnaps(trackingManager.progress.get(i-1).pointOfTime, trackingManager.progress.get(i).pointOfTime);
			}
			MyRectangle rectangle = new MyRectangle(timeOfSnap*1.0+5, 40.0, trackingManager.progress.get(i));
			rectangle.setController(this);
			hboxTracking.getChildren().add(rectangle);
		}
	}
	
	
	void setStage(Stage stage) {
		this.stage = stage;
	}
    
}
