package tddtrainer.catalog;

import com.google.gson.*;

import java.lang.reflect.Type;

public class KatalogExercise {

    static final Deserializer DESERIALIZER = new Deserializer();

    private static final int DEFAULT_BABY_STEPS_CODE_TIME = 180;
    private static final int DEFAULT_BABY_STEPS_TEST_TIME = 180;
    private static final boolean DEFAULT_RETROSPECTIVE = false;

    private final String name;
    private final KatalogPath description;
    private final KatalogPath code;
    private final KatalogPath test;
    private final boolean babyStepsActivated;
    private final int babyStepsCodeTime;
    private final int babyStepsTestTime;
    private final boolean retrospective;

    public KatalogExercise(String name, KatalogPath description, KatalogPath code, KatalogPath test, boolean babyStepsActivated, int babyStepsCodeTime, int babyStepsTestTime, boolean retrospective) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.test = test;
        this.babyStepsActivated = babyStepsActivated;
        this.babyStepsCodeTime = babyStepsCodeTime;
        this.babyStepsTestTime = babyStepsTestTime;
        this.retrospective = retrospective;
    }

    public Exercise load(KatalogLocator locator) {
        Exercise e = new Exercise();

        e.setName(name);
        e.setDescription(String.join("\n", locator.locateAndReadLines(description.appendSuffix(".html"))));
        e.setCode(new JavaClass(code.getPath(), locator.locateAndReadLines(code.appendSuffix(".java"))));
        e.setTest(new JavaClass(test.getPath(), locator.locateAndReadLines(test.appendSuffix(".java"))));
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
            KatalogPath description = context.deserialize(get(o, "description"), KatalogPath.class);
            KatalogPath code = context.deserialize(get(o, "code"), KatalogPath.class);
            KatalogPath test = context.deserialize(get(o, "test"), KatalogPath.class);
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
