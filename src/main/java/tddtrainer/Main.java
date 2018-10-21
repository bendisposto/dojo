package tddtrainer;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tddtrainer.events.LanguageChangeEvent;
import tddtrainer.events.Views;
import tddtrainer.gui.RetrospectViewerController;
import tddtrainer.gui.RetrospectiveController;
import tddtrainer.gui.RootLayoutController;
import tddtrainer.gui.catalog.ExerciseSelectorController;
import tddtrainer.gui.catalog.ExerciseViewerController;

import javax.tools.ToolProvider;
import java.util.Optional;

/**
 * The Main Class to get the Application started.
 */
public class Main extends Application {

    private final EventBus bus;
    private final RootLayoutController workingWindow;
    private final ExerciseSelectorController exerciseSelectionWindow;
    private final RetrospectiveController retrospect;
    private final ExerciseViewerController viewer;
    private final RetrospectViewerController retroViewer;
    Logger logger = LoggerFactory.getLogger(Main.class);
    private Stage primaryStage;

    @Inject
    public Main(EventBus bus,
                ExerciseSelectorController exerciseSelector, RootLayoutController root,
                RetrospectiveController retrospect, ExerciseViewerController viewer,
                RetrospectViewerController retroViewer) {
        this.bus = bus;
        this.exerciseSelectionWindow = exerciseSelector;
        this.workingWindow = root;
        this.viewer = viewer;
        this.retrospect = retrospect;
        this.retroViewer = retroViewer;
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
        primaryStage.setOnCloseRequest((e) -> {
            e.consume();
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Closing Application");
            alert.setHeaderText("Do you really want to close the application?");
            alert.setContentText(
                    "If you click ok, the application will be closed!");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                primaryStage.close();
                System.exit(0);
            }
        });

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
    public void initRootLayout(LanguageChangeEvent event) {
        // loader.setLocation(Main.class.getResource("gui/RootLayout.fxml"));
        // rootLayout = (BorderPane) loader.load();
        // RootLayoutController controller = loader.getController();
        // controller.init(phaseManager, bus);

        StackPane root = new StackPane(workingWindow, exerciseSelectionWindow, retrospect, viewer, retroViewer);
        bus.post(Views.SELECTOR);

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        int height = (int) Math.max(800, 0.8 * visualBounds.getHeight());
        int width = (int) Math.max(1100, 0.8 * visualBounds.getWidth());

        primaryStage.setScene(new Scene(root));
        primaryStage.setWidth(width);
        primaryStage.setMinWidth(1024);
        primaryStage.setHeight(height);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

}
