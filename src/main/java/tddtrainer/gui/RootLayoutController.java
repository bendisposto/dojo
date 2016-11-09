package tddtrainer.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import tddtrainer.automaton.CanProceedEvent;
import tddtrainer.catalog.Exercise;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.LanguageChangeEvent;
import tddtrainer.events.TimeEvent;
import tddtrainer.events.Views;
import tddtrainer.events.automaton.ProceedPhaseRequest;
import tddtrainer.events.automaton.ResetPhaseEvent;
import tddtrainer.events.automaton.SwitchedToGreenEvent;
import tddtrainer.events.automaton.SwitchedToRedEvent;
import tddtrainer.events.automaton.SwitchedToRefactorEvent;
import tddtrainer.events.gui.ShowSelectDialogRequest;
import tddtrainer.handbook.Handbook;
import tddtrainer.tracker.Tracker;

public class RootLayoutController extends BorderPane implements Initializable {

    @FXML
    private BorderPane root;

    @FXML
    private Button resetButton;

    @FXML
    private Button nextStepButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label exerciseLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private ImageView timerImage;

    @FXML
    private HBox iRedBox;

    @FXML
    private Label iRedLabel;

    @FXML
    private MenuItem showDescription;

    @FXML
    private MenuItem zoomin;

    @FXML
    private MenuItem zoomout;

    private ResourceBundle resources;

    private EventBus bus;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private EditorViewController editors;

    Logger logger = LoggerFactory.getLogger(RootLayoutController.class);

    private Exercise exercise;

    private final Tracker tracker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        enableReset(false);
        enableShowDescription(false);
        hideRedBox();
        setKeyboardAccelerators();
    }

    private void setKeyboardAccelerators() {
        zoomin.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.SHORTCUT_DOWN));
        zoomout.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT9, KeyCombination.SHORTCUT_DOWN));
    }

    @Inject
    public RootLayoutController(FXMLLoader loader, EventBus bus, Tracker tracker) {
        this.bus = bus;
        this.tracker = tracker;
        bus.register(this);
        URL resource = getClass().getResource("RootLayout.fxml");
        loader.setLocation(resource);
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            logger.error("Error loading Root view", e);
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
        }

    }

    @FXML
    private void increaseFontSize(ActionEvent event) {
        editors.zoomIn();
    }

    @FXML
    private void decreaseFontSize(ActionEvent event) {
        editors.zoomOut();
    }

    @FXML
    private void resetFontSize(ActionEvent event) {
        editors.zoomDefault();
    }

    @Subscribe
    public void switchToStatusRed(SwitchedToRedEvent event) {
        nextStepButton.setText("Switch to phase GREEN");
        nextStepButton.setStyle("-fx-background-color: green");
        showRedBox();
        iRedLabel.setText("Write exactly one failing test");
        enableReset(false);
        // statusLabel.setText("red");
        // statusLabel.getStyleClass().clear();
        // statusLabel.getStyleClass().add("statuslabel-red");
    }

    @Subscribe
    public void switchToStatusGreen(SwitchedToGreenEvent event) {
        nextStepButton.setText("Switch to phase REFACTOR");
        nextStepButton.setStyle("-fx-background-color: #6f8391");

        enableReset(true);
        hideRedBox();
        // statusLabel.setText("green");
        // statusLabel.getStyleClass().clear();
        // statusLabel.getStyleClass().add("statuslabel-green");
    }

    @Subscribe
    public void switchToStatusRefactor(SwitchedToRefactorEvent event) {
        nextStepButton.setText("Switch to phase RED");
        nextStepButton.setStyle("-fx-background-color: red");

        enableReset(false);
        showRedBox();
        iRedLabel.setText("Modify tests, but keep them passing");
        timeLabel.setText("");
        timerImage.setVisible(false);
        // statusLabel.setText("refactor");
        // statusLabel.getStyleClass().clear();
        // statusLabel.getStyleClass().add("statuslabel-refactor");
    }

    @FXML
    private void selectExercise(ActionEvent event) {
        bus.post(new ShowSelectDialogRequest());
    }

    @FXML
    private void showExerciseDescription(ActionEvent event) {
        bus.post(Views.VIEWER);
    }

    @FXML
    private void showProgress(ActionEvent event) {
        bus.post(Views.RETROSPECT_VIEWER);
    }

    @FXML
    private void reset(ActionEvent event) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Resetting to RED and reverting your code");
        alert.setHeaderText("Do you really want to reset to RED? This will revert your code!");
        alert.setContentText(
                "If you click ok, all recent changes to your code will be reverted.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) {
            return;
        }
        bus.post(new ResetPhaseEvent());
    }

    // @FXML
    // private void handleTutorialMode(ActionEvent event) {
    // CheckMenuItem item = (CheckMenuItem) event.getSource();
    // editorViewController.setTutorialMode(item.isSelected());
    // }

    @FXML
    private void handleNextStep(ActionEvent event) {
        bus.post(new ProceedPhaseRequest());
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
        resetButton.setDisable(!enable);
    }

    protected void enableShowDescription(boolean enable) {
        showDescription.setDisable(!enable);
    }

    @Subscribe
    public void setVisible(Views v) {
        if (v.equals(Views.WORKING)) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    @Subscribe
    public void showExercise(ExerciseEvent exerciseEvent) {
        exercise = exerciseEvent.getExercise();
        bus.post(Views.WORKING);
        if (exercise != null) {
            exerciseLabel.setText(exercise.getName());
            exerciseLabel.setTooltip(new Tooltip(exercise.getName()));
            enableShowDescription(true);
        }
        nextStepButton.setDisable(false);
    }

    @Subscribe
    public void switchProceedButton(CanProceedEvent event) {
        nextStepButton.setDisable(!event.canProceed());
    }

    public void hideRedBox() {
        iRedBox.setVisible(false);
    }

    public void showRedBox() {
        iRedBox.setVisible(true);
        iRedBox.toFront();
    }

}
