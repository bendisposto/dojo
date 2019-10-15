package tddtrainer.catalog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class KatalogDataSource {

    private static final KatalogLocator INDEX_JSON_LOCATOR = new KatalogLocator("katalog-index.json");
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(KatalogLocator.class, KatalogLocator.DESERIALIZER)
            .registerTypeAdapter(KatalogExercise.class, KatalogExercise.DESERIALIZER)
            .create();

    private final KatalogLocatorService rootLocatorService = new KatalogLocatorService();

    public List<Exercise> load() {
        KatalogIndex index = rootLocatorService.locateAndReadJson(INDEX_JSON_LOCATOR, GSON, KatalogIndex.class);
        return index.load(GSON, rootLocatorService);
    }

    public static void main(String[] args) {
        KatalogDataSource src = new KatalogDataSource();
        List<Exercise> exercises = src.load();
    }
}
