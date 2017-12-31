package tddtrainer.gui.catalog;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import tddtrainer.catalog.Exercise;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.Views;

public class ExerciseViewerController extends BorderPane {

    @FXML
    private WebView descriptionField;

    @FXML
    private Button okButton;

    private EventBus bus;

    Logger logger = LoggerFactory.getLogger(ExerciseViewerController.class);
    private Exercise exercise;

    @Inject
    public ExerciseViewerController(FXMLLoader loader, EventBus bus) {
        this.bus = bus;
        this.bus.register(this);
        URL resource = getClass().getResource("ExerciseViewer.fxml");
        loader.setLocation(resource);
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            logger.error("Error loading Exercise viewer view", e);
        }
    }

    public void okButtonAction() {
        bus.post(Views.WORKING);
    }

    @Subscribe
    public void showExerciseSelector(ExerciseEvent event) {
        exercise = event.getExercise();
        descriptionField.getEngine().loadContent(exercise.getDescription());
    }

    @Subscribe
    public void setVisible(Views v) {
        if (v.equals(Views.VIEWER)) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

}
