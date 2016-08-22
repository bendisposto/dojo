package tddtrainer.gui.catalog;

import java.io.IOException;
import java.util.ResourceBundle;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tddtrainer.catalog.CatalogDatasourceIF;
import tddtrainer.catalog.Exercise;
import tddtrainer.events.LanguageChangeEvent;

/**
 * Provides an gui window where a user can select an Exercise from a exercise
 * catalog
 * 
 * @author Marcel
 */
public class ExerciseSelector {

	private CatalogDatasourceIF dataSource;
	private ResourceBundle bundle;

	/**
	 * Creates an ExerciseSelector
	 * 
	 * @param dataSource
	 *            the data source from where the catalog should be read
	 */
	@Inject
	public ExerciseSelector(CatalogDatasourceIF dataSource) {
		this.dataSource = dataSource;
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ExerciseSelector.fxml"));
			loader.setResources(bundle);
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

	@Subscribe
	public void setResourceBundle(LanguageChangeEvent event) {
		this.bundle = event.getBundle();
	}
}
