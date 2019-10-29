package tddtrainer.catalog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * This class fetches the resources for the catalog at the default location and creates the free exercise.
 * It is used for loading the catalog.
 */
public class KatalogDataSource {

    private static final KatalogPath INDEX_JSON_PATH = new KatalogPath("katalog-index.json");
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(KatalogPath.class, KatalogPath.DESERIALIZER)
            .registerTypeAdapter(KatalogExercise.class, KatalogExercise.DESERIALIZER)
            .create();

    private final KatalogLocator rootLocator = new KatalogLocator();

    /**
     * Here we load the catalog from previous set DEFAULT_LOCATION.
     *
     * @return A list of exercises that represent the catalog
     */
    public List<Exercise> loadKatalog() {
        KatalogIndex index = rootLocator.locateAndReadJson(INDEX_JSON_PATH, GSON, KatalogIndex.class);
        List<Exercise> exercises = new ArrayList<>(index.load(GSON, rootLocator));
        exercises.add(createFreeExercise());
        return exercises;
    }

    /**
     * This method creates the free exercise environment for the TDDT,
     * it starts with a test that fails on purpose.
     *
     * @return {@link Exercise} exercise which is a free exercise
     */
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
