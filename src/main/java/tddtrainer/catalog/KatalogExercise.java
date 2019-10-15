package tddtrainer.catalog;

import com.google.gson.*;

import java.lang.reflect.Type;

public class KatalogExercise {

    static final Deserializer DESERIALIZER = new Deserializer();

    private static final int DEFAULT_BABY_STEPS_CODE_TIME = 180;
    private static final int DEFAULT_BABY_STEPS_TEST_TIME = 180;
    private static final boolean DEFAULT_RETROSPECTIVE = false;

    private final String name;
    private final KatalogLocator description;
    private final KatalogLocator code;
    private final KatalogLocator test;
    private final boolean babyStepsActivated;
    private final int babyStepsCodeTime;
    private final int babyStepsTestTime;
    private final boolean retrospective;

    public KatalogExercise(String name, KatalogLocator description, KatalogLocator code, KatalogLocator test, boolean babyStepsActivated, int babyStepsCodeTime, int babyStepsTestTime, boolean retrospective) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.test = test;
        this.babyStepsActivated = babyStepsActivated;
        this.babyStepsCodeTime = babyStepsCodeTime;
        this.babyStepsTestTime = babyStepsTestTime;
        this.retrospective = retrospective;
    }

    public Exercise load(KatalogLocatorService locatorService) {
        Exercise e = new Exercise();

        e.setName(name);
        e.setDescription(String.join("\n", locatorService.locateAndReadLines(new KatalogLocator(description.getPath() + ".html"))));
        e.setCode(new JavaClass(code.getPath(), locatorService.locateAndReadLines(new KatalogLocator(code.getPath() + ".java"))));
        e.setTest(new JavaClass(test.getPath(), locatorService.locateAndReadLines(new KatalogLocator(test.getPath() + ".java"))));
        e.setBabyStepsActivated(babyStepsActivated);
        e.setBabyStepsCodeTime(babyStepsCodeTime);
        e.setBabyStepsTestTime(babyStepsTestTime);
        e.setRetrospective(retrospective);

        return e;
    }

    private static final class Deserializer implements JsonDeserializer<KatalogExercise> {

        @Override
        public KatalogExercise deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject o = json.getAsJsonObject();

            String name = get(o, "name").getAsString();
            KatalogLocator description = context.deserialize(get(o, "description"), KatalogLocator.class);
            KatalogLocator code = context.deserialize(get(o, "code"), KatalogLocator.class);
            KatalogLocator test = context.deserialize(get(o, "test"), KatalogLocator.class);
            boolean babyStepsActivated = get(o, "babyStepsActivated").getAsBoolean();
            int babyStepsCodeTime = o.has("babyStepsCodeTime") ? o.get("babyStepsCodeTime").getAsInt() : DEFAULT_BABY_STEPS_CODE_TIME;
            int babyStepsTestTime = o.has("babyStepsTestTime") ? o.get("babyStepsTestTime").getAsInt() : DEFAULT_BABY_STEPS_TEST_TIME;
            boolean retrospective = o.has("retrospective") ? o.get("retrospective").getAsBoolean() : DEFAULT_RETROSPECTIVE;

            return new KatalogExercise(name, description, code, test, babyStepsActivated, babyStepsCodeTime, babyStepsTestTime, retrospective);
        }

        private static JsonElement get(JsonObject o, String key) throws JsonParseException {
            JsonElement e = o.get(key);
            if (e == null) {
                throw new JsonParseException("missing key '" + key + "'");
            }
            return e;
        }
    }
}
