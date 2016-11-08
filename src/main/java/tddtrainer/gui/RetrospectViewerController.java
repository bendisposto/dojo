package tddtrainer.gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;

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
import tddtrainer.events.Views;
import tddtrainer.tracker.AbstractStep;
import tddtrainer.tracker.Tracker;

public class RetrospectViewerController extends BorderPane {

    @FXML
    private WebView descriptionField;

    @FXML
    private Button okButton;

    private EventBus bus;

    Logger logger = LoggerFactory.getLogger(RetrospectViewerController.class);
    private Exercise exercise;

    private final Tracker tracker;

    @Inject
    public RetrospectViewerController(FXMLLoader loader, EventBus bus, Tracker tracker) {
        this.bus = bus;
        this.tracker = tracker;
        this.bus.register(this);
        URL resource = getClass().getResource("RetrospectViewer.fxml");
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
        List<AbstractStep> retrospecives = tracker.getSteps();
        for (AbstractStep step : retrospecives) {
            System.out.println(step);
        }
        bus.post(Views.WORKING);
    }

    @Subscribe
    public void setVisible(Views v) {
        if (v.equals(Views.RETROSPECT_VIEWER)) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

}
