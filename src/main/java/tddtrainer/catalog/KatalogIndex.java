package tddtrainer.catalog;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KatalogIndex {

    private static final KatalogPath EXERCISE_JSON_PATH = new KatalogPath("katalog-exercise.json");

    private final List<KatalogPath> exercises;

    public KatalogIndex(List<KatalogPath> exercises) {
        this.exercises = new ArrayList<>(exercises);
    }

    public List<Exercise> load(Gson gson, KatalogLocator locator) {
        return exercises.stream()
                .map(path -> {
                    KatalogLocator exerciseLocator = locator.append(path);
                    KatalogExercise katalogExercise = exerciseLocator.locateAndReadJson(EXERCISE_JSON_PATH, gson, KatalogExercise.class);
                    return katalogExercise.load(exerciseLocator);
                })
                .collect(Collectors.toList());
    }
}
