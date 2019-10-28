package tddtrainer.catalog;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

/**
 * This class fetches the resources for the catalog at the default location and creates the free exercise.
 * It is used for loading the catalog.
 */
public class CatalogDataSource {

    private static final String DEFAULT_LOCATION = "https://bendisposto.github.io/dojo/katalog.json";

    static Gson gson = new Gson();

    /**
     * This method fetches the catalog for the exercises used in the training from the DEFAULT_LOCATION https://bendisposto.github.io/dojo/katalog.json .
     * @return String that represents the contents of the katalog.json file.
     * @throws UnirestException Thrown, if the json file cannot be found at the DEFAULT_LOCATION.
     */
    public String fetchCatalog() throws UnirestException {
        String location = System.getProperty("katalog");
        String loc = location != null ? location : DEFAULT_LOCATION;
        if (loc.startsWith("http")) {
            GetRequest getRequest = Unirest.get(loc);
            HttpResponse<String> asString = getRequest.asString();
            return asString.getBody();
        }
        try {
            return new String(Files.readAllBytes(Paths.get(loc)));
        } catch (IOException e) {
            throw new UnirestException("Could not find Katalog at " + loc);
        }
    }

    /**
     * Here we load the catalog from previous set DEFAULT_LOCATION https://bendisposto.github.io/dojo/katalog.json .
     * @return A list of exercises that represent the catalog
     * @throws JsonSyntaxException Thrown if the katalog.json file is containing syntax errors.
     * @throws UnirestException Thrown if the catalog cannot be fetched in the fetchCatalog method.
     */
    public List<Exercise> loadCatalog() throws JsonSyntaxException, UnirestException {
        Type collectionType = new TypeToken<Collection<Exercise>>(){}.getType();

        Collection<Exercise> es = gson.fromJson(fetchCatalog(), collectionType);
        ArrayList<Exercise> catalog = new ArrayList<>(es.size());
        catalog.addAll(es);
        catalog.add(createFreeExercise());

        return catalog;
    }

    /**
     *  This method creates the free exercise environment for the TDDT,
     *  it starts with a test that fails on purpose.
     *  @return {@link Exercise} exercise which is a free exercise
     */
    private Exercise createFreeExercise() {
        Exercise ex = new Exercise();
        ex.setName("Free Exercise");
        ex.setDescription(
                "<h1>Freie Uebung</h1><p>Tu, was immer du willst. Dieser Modus ist geeignet um TDD an selbstgewaehlten Beispielen auszuprobieren.</p>");
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
