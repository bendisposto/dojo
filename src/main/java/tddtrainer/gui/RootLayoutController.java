package tddtrainer.gui;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tddtrainer.automaton.CanProceedEvent;
import tddtrainer.catalog.Exercise;
import tddtrainer.catalog.JavaClass;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.LanguageChangeEvent;
import tddtrainer.events.TimeEvent;
import tddtrainer.events.Views;
import tddtrainer.events.automaton.*;
import tddtrainer.events.gui.ShowSelectDialogRequest;
import tddtrainer.tracker.Tracker;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class RootLayoutController extends BorderPane implements Initializable {

    private final Tracker tracker;
    Logger logger = LoggerFactory.getLogger(RootLayoutController.class);
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
    private Exercise exercise;

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
        nextStepButton.setStyle("-fx-background-color: orange");

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
    private void enforceRefactoring(ActionEvent event) {
        bus.post(new EnforceRefactoringEvent());
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
    private void newExerciseWizard(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("MyClass");
        dialog.setTitle("Enter Class Name");
        dialog.setHeaderText(
                "Creating a fresh exercise. Warning, this automatically terminates your current exercise!");
        dialog.setContentText("Please enter the name of the class (without .java!):");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            Exercise ex = new Exercise();
            String classname = result.get();
            String testname = classname + "Test";
            ex.setName("Free Exercise");
            JavaClass code = new JavaClass(classname, "public class " + classname + "{\n}");
            JavaClass test = new JavaClass(testname,
                    "import static org.junit.Assert.*;\nimport org.junit.*;\n\npublic class " + testname
                            + "{\n    @Test\n    public void failingTest() {\n        fail(\"I fail on purpose\");\n    }\n}");

            ex.setCode(code);
            ex.setTest(test);
            ex.setDescription("A free exercise, do whatever you want to do!");
            ex.setBabyStepsActivated(false);
            ex.setRetrospective(false);
            bus.post(new ExerciseEvent(ex));

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
