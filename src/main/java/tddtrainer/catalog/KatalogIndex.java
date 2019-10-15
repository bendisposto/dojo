package tddtrainer.catalog;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KatalogIndex {

    private static final KatalogLocator EXERCISE_JSON_LOCATOR = new KatalogLocator("katalog-exercise.json");

    private final List<KatalogLocator> exercises;

    public KatalogIndex(List<KatalogLocator> exercises) {
        this.exercises = new ArrayList<>(exercises);
    }

    public List<Exercise> load(Gson gson, KatalogLocatorService locatorService) {
        return exercises.stream()
                .map(l -> {
                    KatalogLocatorService exerciseLocator = locatorService.append(l);
                    KatalogExercise katalogExercise = exerciseLocator.locateAndReadJson(EXERCISE_JSON_LOCATOR, gson, KatalogExercise.class);
                    return katalogExercise.load(exerciseLocator);
                })
                .collect(Collectors.toList());
    }
}
