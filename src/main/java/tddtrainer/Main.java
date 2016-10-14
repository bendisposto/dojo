package tddtrainer;

import java.io.IOException;

import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tddtrainer.events.LanguageChangeEvent;
import tddtrainer.events.Views;
import tddtrainer.gui.RootLayoutController;
import tddtrainer.gui.catalog.ExerciseSelectorController;

/**
 * The Main Class to get the Application started.
 *
 */
public class Main extends Application {

    private Stage primaryStage;

    Logger logger = LoggerFactory.getLogger(Main.class);
    private final EventBus bus;
    private final RootLayoutController workingWindow;
    private final ExerciseSelectorController exerciseSelectionWindow;

    @Inject
    public Main(EventBus bus,
            ExerciseSelectorController exerciseSelector, RootLayoutController root) {
        this.bus = bus;
        this.exerciseSelectionWindow = exerciseSelector;
        this.workingWindow = root;
        bus.register(exerciseSelector);
    }

    @Override
    public void start(Stage primaryStage) {
        logger.trace("Checking if compiler is present.");
        checkForJdk();

        logger.trace("Setting up event bus.");
        bus.register(this);

        logger.trace("Setting up primary stage.");
        this.primaryStage = primaryStage;
        primaryStage.setTitle("TDD Trainer");
        primaryStage.getIcons().add(new Image("/tddtrainer/gui/app_icon.png"));
        primaryStage.setOnCloseRequest((e) -> System.exit(0));

        bus.post(new LanguageChangeEvent(null));
    }

    private void checkForJdk() {
        logger.trace("Checking if compiler is present");
        if (ToolProvider.getSystemJavaCompiler() == null) {
            logger.error("JDK not present, ToolProvider.getSystemJavaCompiler() returned null");
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error: No Java Compiler");
            alert.setHeaderText(null);
            alert.setContentText("Cannot execute application, because a java compiler is required.\n\n"
                    + "Please run the application with a JDK of version 1.8.0_40 or higher.\n\n");
            alert.showAndWait();
            System.exit(-1);
        }
    }

    @Subscribe
    public void initRootLayout(LanguageChangeEvent event) throws IOException {
        // loader.setLocation(Main.class.getResource("gui/RootLayout.fxml"));
        // rootLayout = (BorderPane) loader.load();
        // RootLayoutController controller = loader.getController();
        // controller.init(phaseManager, bus);

        StackPane root = new StackPane(workingWindow, exerciseSelectionWindow);
        bus.post(Views.SELECTOR);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setWidth(1100);
        primaryStage.setMinWidth(1100);
        primaryStage.setHeight(800);
        primaryStage.setMinHeight(600);
    }

}