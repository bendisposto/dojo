package tddtrainer.catalog;

import com.google.gson.*;

import java.lang.reflect.Type;

public class KatalogPath {

    public static final KatalogPath EMPTY = new KatalogPath("");

    static final Deserializer DESERIALIZER = new Deserializer();

    private final String path;

    public KatalogPath(String path) {
        if (path.startsWith("/")) {
            throw new IllegalArgumentException("path must not be absolute");
        }
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public KatalogPath append(KatalogPath child) {
        if (path.isEmpty()) {
            return child;
        } else if (child.path.isEmpty()) {
            return this;
        }
        return new KatalogPath(path + (path.endsWith("/") ? "" : "/") + child.path);
    }

    public KatalogPath appendSuffix(String suffix) {
        if (suffix.contains("/")) {
            throw new IllegalArgumentException("suffix may not contain separators");
        }
        return new KatalogPath(path + suffix);
    }

    private static class Deserializer implements JsonDeserializer<KatalogPath> {

        @Override
        public KatalogPath deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                return new KatalogPath(get(json.getAsJsonObject(), "path").getAsString());
            }

            return new KatalogPath(json.getAsString());
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
