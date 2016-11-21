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

public class CatalogDataSource {

    private static final String DEFAULT_LOCATION = "https://www3.hhu.de/stups/downloads/dojo/katalog.json";

    static Gson gson = new Gson();

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

    public List<Exercise> loadCatalog() throws JsonSyntaxException, UnirestException {
        Type collectionType = new TypeToken<Collection<Exercise>>() {
        }.getType();

        Collection<Exercise> es = gson.fromJson(fetchCatalog(), collectionType);
        ArrayList<Exercise> catalog = new ArrayList<>(es.size());
        catalog.addAll(es);
        catalog.add(createFreeExercise());

        return catalog;
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
