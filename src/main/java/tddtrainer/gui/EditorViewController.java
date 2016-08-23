package tddtrainer.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import tddtrainer.catalog.Exercise;
import tddtrainer.catalog.JavaClass;
import tddtrainer.events.ExecutionResultEvent;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.PhaseChangeEvent;
import tddtrainer.logic.Phase;
import tddtrainer.logic.PhaseManagerIF;
import tddtrainer.logic.PhaseStatus;

public class EditorViewController extends SplitPane implements Initializable {

	private JavaCodeArea tests;
	private JavaCodeArea code;

	@FXML
	private TextArea console;

	@FXML
	private AnchorPane codePane;

	@FXML
	private AnchorPane testPane;

	@FXML
	private Label codeLabel;

	@FXML
	private Label testLabel;

	@FXML
	private HBox iGreenBox;

	@FXML
	private HBox codeBox;

	private PhaseManagerIF phaseManager;

	Logger logger = LoggerFactory.getLogger(EditorViewController.class);

	@Inject
	public EditorViewController(FXMLLoader loader, EventBus bus, PhaseManagerIF phaseManager) {
		this.phaseManager = phaseManager;
		bus.register(this);
		URL resource = getClass().getResource("EditorView.fxml");
		loader.setLocation(resource);
		loader.setController(this);
		loader.setRoot(this);
		try {
			loader.load();
		} catch (IOException e) {
			logger.error("Error loading Root view", e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		iGreenBox.setVisible(false);
		addEditors();
		AnchorPane.setBottomAnchor(this, 0.0);
		AnchorPane.setLeftAnchor(this, 5.0);
		AnchorPane.setRightAnchor(this, 5.0);
		AnchorPane.setTopAnchor(this, 60.0);

	}

	protected void init(PhaseManagerIF phaseManager, EventBus bus) {
		bus.register(this);
	}

	@Subscribe
	public void showExercise(ExerciseEvent exerciseEvent) {
		Exercise exercise = exerciseEvent.getExercise();
		if (exercise != null) {
			showExercise(exercise);
			changePhase(new PhaseChangeEvent(phaseManager.checkPhase(exercise, false).getPhase()));
		}
	}

	public String getCode() {
		return code.getText();
	}

	public String getTest() {
		return tests.getText();
	}

	public void showExercise(Exercise exercise) {
		for (JavaClass jclass : exercise.getCode()) {
			// boolean wasDisabled = code.isDisable();
			// code.setDisable(false);
			code.clear();
			code.appendText(jclass.getCode());
			codeLabel.setText(jclass.getName());
			// code.setDisable(wasDisabled);
		}

		for (JavaClass jclass : exercise.getTests()) {
			// boolean wasDisabled = code.isDisable();
			// tests.setDisable(false);
			tests.clear();
			tests.appendText(jclass.getCode());
			testLabel.setText(jclass.getName());
			// tests.setDisable(wasDisabled);
		}
	}

	@Subscribe
	void changePhase(PhaseChangeEvent phaseChangeEvent) {
		Phase phase = phaseChangeEvent.getPhase();
		switch (phase) {
		case RED:
			changePhaseToRed();
			break;
		case GREEN:
			changePhaseToGreen();
			break;
		case REFACTOR:
			changePhaseToRefactor();
			break;
		}
	}

	private void changePhaseToRed() {
		code.disable(true);
		tests.disable(false);
		tests.setStyle("-fx-border-color: crimson;");
		code.setStyle("-fx-border-color: transparent;");
		iGreenBox.setVisible(false);
		AnchorPane.setRightAnchor(codeBox, 15.0);
	}

	private void changePhaseToGreen() {
		code.disable(false);
		tests.disable(true);
		code.setStyle("-fx-border-color: forestgreen;");
		tests.setStyle("-fx-border-color: transparent;");
		iGreenBox.setVisible(true);
		AnchorPane.setRightAnchor(codeBox, iGreenBox.getWidth() + 10);
	}

	private void changePhaseToRefactor() {
		code.disable(false);
		tests.disable(false);
		tests.setStyle("-fx-border-color: grey;");
		code.setStyle("-fx-border-color: grey;");
		iGreenBox.setVisible(false);
		AnchorPane.setRightAnchor(codeBox, 15.0);
	}

	private void addEditors() {
		code = new JavaCodeArea();
		code.setEditable(false);
		codePane.getChildren().add(code);
		AnchorPane.setTopAnchor(code, 50.0);
		AnchorPane.setLeftAnchor(code, 20.0);
		AnchorPane.setRightAnchor(code, 20.0);
		AnchorPane.setBottomAnchor(code, 5.0);

		tests = new JavaCodeArea();
		tests.setEditable(false);
		testPane.getChildren().add(tests);
		AnchorPane.setTopAnchor(tests, 50.0);
		AnchorPane.setLeftAnchor(tests, 20.0);
		AnchorPane.setRightAnchor(tests, 20.0);
		AnchorPane.setBottomAnchor(tests, 5.0);
	}

	@Subscribe
	public void showExecutionResult(ExecutionResultEvent event) {
		PhaseStatus status = event.getPhaseStatus();
		console.setText(status.getExecutionResultAsString());

		if (status.isValid()) {
			console.setStyle("-fx-text-fill: grey");
		} else {
			console.setStyle("-fx-text-fill: red");
		}
	}

	// protected void setTutorialMode(boolean selected) {
	// tutorialMode = selected;
	// if (!selected) {
	// rootLayoutController.hideRedBox();
	// rootLayoutController.iRedBox.setVisible(false);
	// iGreenBox.setVisible(false);
	// AnchorPane.setRightAnchor(codeBox, 15.0);
	// }
	// phaseManager.resetPhase();
	// }

}
