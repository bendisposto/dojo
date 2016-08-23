package tddtrainer.gui.catalog;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tddtrainer.catalog.CatalogDatasourceIF;
import tddtrainer.catalog.Exercise;

/**
 * Provides an gui window where a user can select an Exercise from a exercise
 * catalog
 * 
 * @author Marcel
 */
public class ExerciseSelector {

	private CatalogDatasourceIF dataSource;
	private Provider<ResourceBundle> bundleProvider;

	/**
	 * Creates an ExerciseSelector
	 * 
	 * @param dataSource
	 *            the data source from where the catalog should be read
	 */
	@Inject
	public ExerciseSelector(CatalogDatasourceIF dataSource, Provider<ResourceBundle> bundleProvider) {
		this.dataSource = dataSource;
		this.bundleProvider = bundleProvider;
	}

	public CatalogDatasourceIF getDataSource() {
		return dataSource;
	}

	/**
	 * Opens a new modal window where the user can select an Exercise from the
	 * catalog
	 * 
	 * @return the selected Exercise or null if the dialog is canceled
	 */
	public Exercise selectExercise() {
		Stage dialogStage = new Stage();

		try {
			URL location = getClass().getResource("ExerciseSelector.fxml");
			FXMLLoader loader = new FXMLLoader(location);
			loader.setResources(bundleProvider.get());
			BorderPane pane = (BorderPane) loader.load();
			ExerciseSelectorController controller = (ExerciseSelectorController) loader.getController();
			controller.setDatasource(dataSource);
			controller.setStage(dialogStage);
			controller.loadData();

			dialogStage.setScene(new Scene(pane));
			dialogStage.sizeToScene();
			dialogStage.setResizable(false);
			dialogStage.showAndWait();

			return controller.getSelectedExercise();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
