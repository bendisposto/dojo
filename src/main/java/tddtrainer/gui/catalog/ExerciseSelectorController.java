package tddtrainer.gui.catalog;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tddtrainer.catalog.Exercise;
import tddtrainer.catalog.KatalogDataSource;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.Views;
import tddtrainer.events.gui.ShowSelectDialogRequest;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExerciseSelectorController extends BorderPane {

    @FXML
    private ListView<Exercise> exerciseList;
    @FXML
    private WebView descriptionField;
    @FXML
    private Button selectButton;
    @FXML
    private Button cancelButton;

    private ObjectProperty<Exercise> selectedExercise = new SimpleObjectProperty<>();
    private EventBus bus;

    Logger logger = LoggerFactory.getLogger(ExerciseSelectorController.class);
    private final KatalogDataSource datasource;

    @Inject
    public ExerciseSelectorController(FXMLLoader loader, EventBus bus, KatalogDataSource datasource) {
        this.bus = bus;
        this.datasource = datasource;
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

    private void showFailedToLoadCatalogAlert(Exception e) {
        logger.error("Error fetching catalog.", e);
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error fetching catalog");
        alert.setHeaderText(null);

        alert.setContentText(String.format(
                "The catalog could not be downloaded. \n\n"
                        + "Encountered exception:\n%s\n"
                        + (e.getCause() != null ? "Caused by: %s\n" : "")
                        + "\n"
                        + "Please verify that you are online. "
                        + "If the problem still occurs please notify the administrator. \n\n"
                        + "The program will now be terminated.",
                e, e.getCause()));
        alert.showAndWait();
        System.exit(-1);
    }

    @FXML
    public void initialize() {

        cancelButton.disableProperty().bind(selectedExercise.isNull());

        List<Exercise> catalog;
        try {
            catalog = datasource.loadKatalog();
        } catch (Exception e) {
            showFailedToLoadCatalogAlert(e);
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
            descriptionField.getEngine().loadContent(newValue.getDescription());
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

        exerciseList.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                selectButtonAction();
            }
        });

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
        // This shouldn't be null since you shouldn't be able to trigger this
        // function if no exercise is selected.
        Exercise selectedExercise = exerciseList.getSelectionModel().getSelectedItem();
        if (selectedExercise != null) {
            this.selectedExercise.set(selectedExercise);
            bus.post(new ExerciseEvent(selectedExercise));
        }
    }

    public void cancelButtonAction() {
        bus.post(Views.WORKING);
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
