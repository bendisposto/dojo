package tddtrainer.catalog;

import com.google.gson.*;

import java.lang.reflect.Type;

public class KatalogLocator {

    public static final KatalogLocator EMPTY = new KatalogLocator("");

    static final Deserializer DESERIALIZER = new Deserializer();

    private final String path;

    public KatalogLocator(String path) {
        if (path.startsWith("/")) {
            throw new IllegalArgumentException("path must not start with '/'");
        }
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public KatalogLocator append(KatalogLocator locator) {
        if (path.isEmpty()) {
            return locator;
        } else if (locator.path.isEmpty()) {
            return this;
        }
        return new KatalogLocator(path + (path.endsWith("/") ? "" : "/") + locator.path);
    }

    private static class Deserializer implements JsonDeserializer<KatalogLocator> {

        @Override
        public KatalogLocator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                return new KatalogLocator(get(json.getAsJsonObject(), "path").getAsString());
            }

            return new KatalogLocator(json.getAsString());
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
