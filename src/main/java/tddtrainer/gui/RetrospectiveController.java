package tddtrainer.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import tddtrainer.events.Views;
import tddtrainer.events.automaton.ProceedPhaseRequest;
import tddtrainer.tracker.RetrospectiveStep;
import tddtrainer.tracker.Tracker;

public class RetrospectiveController extends BorderPane implements Initializable {

    Logger logger = LoggerFactory.getLogger(RootLayoutController.class);
    private final EventBus bus;

    @FXML
    TextArea retro;
    @FXML
    TextArea plan;

    private final Tracker tracker;

    @Inject
    public RetrospectiveController(FXMLLoader loader, EventBus bus, Tracker tracker) {
        this.bus = bus;
        this.tracker = tracker;
        bus.register(this);
        URL resource = getClass().getResource("retrospective.fxml");
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
    public void setVisible(Views v) {
        if (v.equals(Views.RETROSPECT)) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void continueClick(ActionEvent event) {
        tracker.addStep(new RetrospectiveStep(null, retro.getText(), plan.getText()));
        bus.post(new ProceedPhaseRequest());
        bus.post(Views.WORKING);
        plan.clear();
        retro.clear();
    }

}
