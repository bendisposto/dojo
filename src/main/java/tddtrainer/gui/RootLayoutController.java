package tddtrainer.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import tddtrainer.catalog.Exercise;
import tddtrainer.events.LanguageChangeEvent;
import tddtrainer.events.TimeEvent;
import tddtrainer.handbook.Handbook;
import tddtrainer.logic.PhaseManagerIF;
import tddtrainer.logic.PhaseStatus;

public class RootLayoutController implements Initializable {

	@FXML
	private BorderPane root;
	
    @FXML
    private Button reset;
    
	@FXML
	Label statusLabel;

	@FXML
	Label exerciseLabel;
    
	@FXML
	Label timeLabel;

	@FXML
	Button nextStepButton;

	@FXML
	HBox iRedBox;

    @FXML
    private MenuItem showDescription;
    
    @FXML
    ImageView timerImage;

	private ResourceBundle resources;

	private PhaseManagerIF phaseManager;

	private EventBus bus;

	private EditorViewController editorViewController;

    @FXML
    private AnchorPane rootPane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
	}

	public void init(PhaseManagerIF phaseManager, EventBus bus) {
		this.phaseManager = phaseManager;
		this.bus = bus;
		bus.register(this);
		showEditorView();
	}

	private void showEditorView() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("EditorView.fxml"));
			loader.setResources(resources);
			SplitPane editorView = loader.load();
			rootPane.getChildren().add(editorView);
			AnchorPane.setBottomAnchor(editorView, 0.0);
			AnchorPane.setLeftAnchor(editorView, 5.0);
			AnchorPane.setRightAnchor(editorView, 5.0);
			AnchorPane.setTopAnchor(editorView, 60.0);
			editorViewController = loader.getController();
			bus.register(editorViewController);
			editorViewController.init(phaseManager, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Subscribe
	public void updateTime(TimeEvent event) {
		long time = event.getTime();
		Platform.runLater(() -> {
			timerImage.setVisible(true);
			timeLabel.setText("" + time);
			if (time <= 5) {
				timeLabel.setFont(new Font("System bold", 18.0));
			} else if (time <= 10) {
				timeLabel.setFont(new Font("System", 15.0));
				timeLabel.setStyle("-fx-text-fill: crimson");
			} else {
				timeLabel.setFont(new Font("System", 15.0));
				timeLabel.setStyle("-fx-text-fill: #6f8391");
			}
		});
	}

	@FXML
	private void changeLanguage(ActionEvent event) {
		List<String> choices = new ArrayList<>();
		choices.add("English");
		choices.add("Deutsch");

		ChoiceDialog<String> dialog = new ChoiceDialog<>("English", choices);
		dialog.setTitle(resources.getString("languagedialog.title"));
		dialog.setHeaderText(resources.getString("languagedialog.headerText"));
		dialog.setContentText(resources.getString("languagedialog.contentText"));

		Optional<String> result = dialog.showAndWait();

		Locale locale = result.isPresent() && result.get().equals("English") ? new Locale("en", "EN")
				: result.isPresent() && result.get().equals("Deutsch") ? new Locale("de", "DE") : resources.getLocale();

		if (!resources.getLocale().toString().equals(locale.toString().substring(0, 2))) {

			this.resources = ResourceBundle.getBundle("bundles.tddt", locale);
			bus.post(new LanguageChangeEvent(resources));
			phaseManager.resetPhase();
		}

	}

	@FXML
	private void selectExercise(ActionEvent event) {
		phaseManager.selectExercise();
	}
	
	@FXML
	private void showProgress(ActionEvent event) {
		phaseManager.displayTracking();
	}
	
	@FXML
	private void reset(ActionEvent event) {
		phaseManager.resetPhase();
	}

	@FXML
	private void handleTutorialMode(ActionEvent event) {
		CheckMenuItem item = (CheckMenuItem) event.getSource();
		editorViewController.setTutorialMode(item.isSelected());
	}
	
	@FXML
	private void handleNextStep(ActionEvent event) {
		Exercise exercise = editorViewController.newExerciseFromCurrentInput();
		PhaseStatus status = phaseManager.checkPhase(exercise, true);
		editorViewController.changePhase(status);
	}
	
	@FXML
	private void showExerciseDescription(ActionEvent event) {
		String description = phaseManager.getOriginalExercise().getDescription();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(resources.getString("description"));
		alert.setHeaderText(null);
		alert.setContentText(description);

		alert.showAndWait();
	}
	
	@FXML
	private void showHandbook(ActionEvent event) {
		try {
			new Handbook().showPDF();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(resources.getString("handbook.error.title"));
			alert.setHeaderText(null);
			alert.setContentText(resources.getString("handbook.error.message") + "\n" + e.getMessage());
			alert.showAndWait();
		}
	}

	protected void enableReset(boolean enable) {
		reset.setDisable(!enable);
	}
	
	protected void enableShowDescription(boolean enable) {
		showDescription.setDisable(!enable);
	}

}
