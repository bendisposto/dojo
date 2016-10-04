package tddtrainer.catalog;

import java.io.InputStream;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Interface for a catalog datasource
 * 
 * @author Marcel
 */
public interface CatalogDatasourceIF {

    /**
     * Loads the {@link Exercise Exercises} from the data source and returns
     * them as a {@link List}
     * 
     * @return a {@link List} with all {@link Exercise Exercises} from the data
     *         source
     */
    public List<Exercise> loadCatalog();

    public void setXmlStream(InputStream xmlStream);

    default ObservableList<Exercise> getEntries() {
        return FXCollections.observableArrayList(loadCatalog());
    }

}
