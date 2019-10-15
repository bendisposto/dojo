package tddtrainer.catalog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class KatalogDataSource {

    private static final KatalogLocator INDEX_JSON_LOCATOR = new KatalogLocator("katalog-index.json");
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(KatalogLocator.class, KatalogLocator.DESERIALIZER)
            .registerTypeAdapter(KatalogExercise.class, KatalogExercise.DESERIALIZER)
            .create();

    private final KatalogLocatorService rootLocatorService = new KatalogLocatorService();

    public List<Exercise> loadKatalog() {
        KatalogIndex index = rootLocatorService.locateAndReadJson(INDEX_JSON_LOCATOR, GSON, KatalogIndex.class);
        List<Exercise> exercises = new ArrayList<>(index.load(GSON, rootLocatorService));
        exercises.add(createFreeExercise());
        return exercises;
    }

    private Exercise createFreeExercise() {
        Exercise ex = new Exercise();
        ex.setName("Free Exercise");
        ex.setDescription(
                "<h1>Freie Übung</h1><p>Tu, was immer du willst. Dieser Modus ist geeignet um TDD an selbstgewählten Beispielen auszuprobieren.</p>");
        String classname = "FreeExercise";
        String testname = "FreeExerciseTest";
        JavaClass code = new JavaClass(classname, "public class " + classname + "{\n}");
        JavaClass test = new JavaClass(testname,
                "import static org.junit.Assert.*;\nimport org.junit.*;\n\npublic class " + testname
                        + " {\n\n    @Test\n    public void failingTest() {\n        fail(\"I fail on purpose\");\n    }\n\n}");

        ex.setCode(code);
        ex.setTest(test);
        ex.setBabyStepsActivated(false);
        ex.setRetrospective(false);
        return ex;
    }
}
