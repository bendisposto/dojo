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
import javafx.scene.layout.BorderPane;
import tddtrainer.events.Views;
import tddtrainer.events.automaton.ProceedPhaseRequest;

public class RetrospectiveController extends BorderPane implements Initializable {

    Logger logger = LoggerFactory.getLogger(RootLayoutController.class);
    private final EventBus bus;

    @Inject
    public RetrospectiveController(FXMLLoader loader, EventBus bus) {
        this.bus = bus;
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
        // TODO Auto-generated method stub
    }

    @FXML
    private void continueClick(ActionEvent event) {
        bus.post(new ProceedPhaseRequest());
        bus.post(Views.WORKING);
    }

}
