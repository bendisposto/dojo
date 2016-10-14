package tddtrainer.gui.catalog;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import tddtrainer.catalog.CatalogDataSource;
import tddtrainer.catalog.Exercise;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.Views;
import tddtrainer.events.gui.ShowSelectDialogRequest;

public class ExerciseSelectorController extends BorderPane {

    @FXML
    private ListView<Exercise> exerciseList;
    @FXML
    private TextArea descriptionField;
    @FXML
    private Button selectButton;
    @FXML
    private Button cancelButton;

    private ObjectProperty<Exercise> selectedExercise = new SimpleObjectProperty<>();
    private EventBus bus;

    Logger logger = LoggerFactory.getLogger(ExerciseSelectorController.class);
    private ExerciseSelector selector;

    @Inject
    public ExerciseSelectorController(FXMLLoader loader, EventBus bus, ExerciseSelector selector) {
        this.bus = bus;
        this.selector = selector;
        this.bus.register(this);
        URL resource = getClass().getResource("ExerciseSelector.fxml");
        loader.setLocation(resource);
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            logger.error("Error loading Exercise selector view", e);
        }
    }

    private void showFailedToLoadCatalogAlert(String exceptionName, String excptionMessage) {
        logger.error("Error fetching catalog. {} : {}", exceptionName, excptionMessage);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error fetching catalog");
        alert.setHeaderText(null);
        alert.setContentText(String.format(
                "The catalog could not be downloaded. \n\n"
                        + "The cause is %s : %s \n\n"
                        + "Please verify that you are online. "
                        + "If the problem still occurs please notify the administrator. \n\n"
                        + "The program will now be terminated.",
                exceptionName, excptionMessage));
        alert.showAndWait();
    }

    @FXML
    public void initialize() {

        cancelButton.disableProperty().bind(selectedExercise.isNull());

        CatalogDataSource dataSource = selector.getDataSource();
        List<Exercise> catalog;
        try {
            catalog = dataSource.loadCatalog();
        } catch (JsonSyntaxException | UnirestException e) {
            showFailedToLoadCatalogAlert(e.getClass().getSimpleName(), e.getMessage());
            catalog = new ArrayList<>();
        }
        exerciseList.setItems(FXCollections.observableArrayList(catalog));

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

        // testTimeLabel.textProperty().bind(Bindings.format("%.0fs",
        // testTimeSlider.valueProperty()));

        Platform.runLater(() -> exerciseList.requestFocus());
    }

    public void selectButtonAction() {
        if (selectedExercise.isNotNull().get()) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Changing exercise");
            alert.setHeaderText("Do you really want to change the exercise?");
            alert.setContentText(
                    "If you click ok, the current exercise will be aborted and all changes are discarded!");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK) {
                return;
            }
        }
        Exercise selectedExercise = exerciseList.getSelectionModel().getSelectedItem();
        if (selectedExercise != null) {
            this.selectedExercise.set(selectedExercise);
        }
        bus.post(new ExerciseEvent(selectedExercise));
    }

    public void cancelButtonAction() {
        bus.post(Views.WORKING);
    }

    public void checkBabySteps() {
    }

    public Exercise getSelectedExercise() {
        return selectedExercise.getValue();
    }

    @Subscribe
    public void showExerciseSelector(ShowSelectDialogRequest event) {
        bus.post(Views.SELECTOR);
    }

    @Subscribe
    public void setVisible(Views v) {
        if (v.equals(Views.SELECTOR)) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

}
