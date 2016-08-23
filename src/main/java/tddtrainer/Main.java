package tddtrainer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tddtrainer.events.LanguageChangeEvent;
import tddtrainer.gui.RootLayoutController;
import tddtrainer.gui.catalog.ExerciseSelector;
import tddtrainer.logic.PhaseManager;
import tddtrainer.logic.PhaseManagerIF;
import tddtrainer.tracking.TrackingManager;

/**
 * The Main Class to get the Application started.
 *
 */
public class Main extends Application {

	private BorderPane rootLayout;
	private Stage primaryStage;
	private final PhaseManagerIF phaseManager;

	Logger logger = LoggerFactory.getLogger(Main.class);
	private String location = "https://gist.githubusercontent.com/bendisposto/22c56ad002e562b14beea0449b981b0d/raw/f968a2dbebc4830ed94e4e47beb25e50c9901288/catalog.xml";
	private final EventBus bus;
	private FXMLLoader loader;
	private RootLayoutController root;

	@Inject
	public Main(FXMLLoader loader, EventBus bus, PhaseManager phaseManager,
			ExerciseSelector exerciseSelector, RootLayoutController root) {
		this.loader = loader;
		this.bus = bus;
		this.phaseManager = phaseManager;
		this.root = root;
		exerciseSelector.getDataSource().setXmlStream(getDatasourceStream());
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
		primaryStage.setTitle("TDDTrainer");
		primaryStage.getIcons().add(new Image("/tddtrainer/gui/app_icon.png"));
		primaryStage.setOnCloseRequest((e) -> System.exit(0));

		TrackingManager trackingManager = new TrackingManager();
		bus.register(trackingManager);

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

	private InputStream getDatasourceStream() {
		InputStream xmlStream = null;
		try {
			logger.debug("Fetch Catalog from {}", location);
			URL url = new URL(location);
			xmlStream = url.openStream();
		} catch (IOException e) {
			showFailedToLoadCatalogAlert(e.getClass().getSimpleName(), e.getLocalizedMessage());
			System.exit(-1);
		}
		return xmlStream;
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

	@Subscribe
	public void initRootLayout(LanguageChangeEvent event) throws IOException {
		// loader.setLocation(Main.class.getResource("gui/RootLayout.fxml"));
		// rootLayout = (BorderPane) loader.load();
		// RootLayoutController controller = loader.getController();
		// controller.init(phaseManager, bus);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		primaryStage.setWidth(1100);
		primaryStage.setMinWidth(1100);
		primaryStage.setHeight(600);
		primaryStage.setMinHeight(600);
	}

}