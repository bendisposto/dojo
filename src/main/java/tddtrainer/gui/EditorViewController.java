package tddtrainer.gui;

import com.google.common.eventbus.Subscribe;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import tddtrainer.catalog.Exercise;
import tddtrainer.catalog.JavaClass;
import tddtrainer.events.ExecutionResultEvent;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.logic.Phase;
import tddtrainer.logic.PhaseManagerIF;
import tddtrainer.logic.PhaseStatus;

public class EditorViewController {

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
	private RootLayoutController rootLayoutController;
	private boolean tutorialMode = true;

	public void initialize() {
		iGreenBox.setVisible(false);
		addEditors();
	}

	protected void init(PhaseManagerIF phaseManager, RootLayoutController rootLayoutController) {
		this.phaseManager = phaseManager;
		this.rootLayoutController = rootLayoutController;
		rootLayoutController.enableReset(false);
		rootLayoutController.enableShowDescription(false);
		rootLayoutController.hideRedBox();
	}

	@Subscribe
	public void showExercise(ExerciseEvent exerciseEvent) {
		Exercise exercise = exerciseEvent.getExercise();
		if (exercise != null) {
			showExercise(exercise);
			changePhase(phaseManager.checkPhase(exercise, false));
		}
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

	void changePhase(PhaseStatus phaseStatus) {
		Phase phase = phaseStatus.getPhase();

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
		rootLayoutController.switchToStatusRed();
		code.disable(true);
		tests.disable(false);
		tests.setStyle("-fx-border-color: crimson;");
		code.setStyle("-fx-border-color: transparent;");
		if (tutorialMode) {
			rootLayoutController.showRedBox();
			iGreenBox.setVisible(false);
		}
		AnchorPane.setRightAnchor(codeBox, 15.0);
	}

	private void changePhaseToGreen() {
		rootLayoutController.switchToStatusGreen();
		code.disable(false);
		tests.disable(true);
		code.setStyle("-fx-border-color: forestgreen;");
		tests.setStyle("-fx-border-color: transparent;");
		if (tutorialMode) {
			rootLayoutController.hideRedBox();
			iGreenBox.setVisible(true);
			AnchorPane.setRightAnchor(codeBox, iGreenBox.getWidth() + 10);
		}
	}

	private void changePhaseToRefactor() {
		rootLayoutController.switchToStatusRefactor();
		code.disable(false);
		tests.disable(false);
		tests.setStyle("-fx-border-color: grey;");
		code.setStyle("-fx-border-color: grey;");
		if (tutorialMode) {
			rootLayoutController.hideRedBox();
			iGreenBox.setVisible(false);
		}
		AnchorPane.setRightAnchor(codeBox, 15.0);
	}

	Exercise newExerciseFromCurrentInput() {
		Exercise oldExercise = phaseManager.getOriginalExercise();
		Exercise exercise = new Exercise(oldExercise.getName(), oldExercise.getDescription());
		exercise.addCode(new JavaClass(oldExercise.getCode(0).getName(), code.getText()));
		exercise.addTest(new JavaClass(oldExercise.getTest(0).getName(), tests.getText()));
		return exercise;
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
