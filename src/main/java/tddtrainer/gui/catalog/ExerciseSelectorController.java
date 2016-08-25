package tddtrainer.gui.catalog;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tddtrainer.catalog.CatalogDatasourceIF;
import tddtrainer.catalog.Exercise;

public class ExerciseSelectorController {

	@FXML
	private ListView<Exercise> exerciseList;
	@FXML
	private TextArea descriptionField;
	@FXML
	private Button selectButton;
	@FXML
	private Button cancelButton;
	@FXML
	private HBox sliderPane;
	@FXML
	private CheckBox activateBabyStepsCheckBox;
	@FXML
	private Label codeTimeLabel;
	@FXML
	private Slider codeTimeSlider;

	private CatalogDatasourceIF datasource;
	private Stage stage;
	private Exercise selectedExercise;

	@FXML
	public void initialize() {

		exerciseList.setCellFactory(param -> {

			ListCell<Exercise> cell = new ListCell<Exercise>() {
				@Override
				protected void updateItem(Exercise item, boolean empty) {
					super.updateItem(item, empty);

					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						setText(item.getName());
					}
				}
			};

			return cell;
		});

		exerciseList.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
			selectButton.setDisable(false);
			descriptionField.setText(newValue.getDescription());
		});

		EventHandler<KeyEvent> eventHandler = (e) -> {
			if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.ESCAPE) {
				exerciseList.getParent().fireEvent(e);
			}
		};

		exerciseList.addEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
		descriptionField.addEventFilter(KeyEvent.KEY_PRESSED, eventHandler);

		codeTimeLabel.textProperty().bind(Bindings.format("%.0fs", codeTimeSlider.valueProperty()));
		// testTimeLabel.textProperty().bind(Bindings.format("%.0fs",
		// testTimeSlider.valueProperty()));

		sliderPane.setVisible(false);
		Platform.runLater(() -> exerciseList.requestFocus());
	}

	public void selectButtonAction() {
		Exercise selectedExercise = exerciseList.getSelectionModel().getSelectedItem();
		if (selectedExercise != null) {
			selectedExercise.setBabyStepsActivated(activateBabyStepsCheckBox.isSelected());
			selectedExercise.setBabyStepsCodeTime((int) codeTimeSlider.getValue());
			this.selectedExercise = selectedExercise;
			stage.close();
		}
	}

	public void cancelButtonAction() {
		selectedExercise = null;
		stage.close();
	}

	public void checkBabySteps() {
		sliderPane.setVisible(activateBabyStepsCheckBox.isSelected());
	}

	public void setDatasource(CatalogDatasourceIF datasource) {
		this.datasource = datasource;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public Exercise getSelectedExercise() {
		return selectedExercise;
	}

	public void loadData() {
		if (datasource == null) {
			System.err.println("datasource is null");
			return;
		}
		exerciseList.setItems(FXCollections.observableArrayList(datasource.loadCatalog()));
	}

}
