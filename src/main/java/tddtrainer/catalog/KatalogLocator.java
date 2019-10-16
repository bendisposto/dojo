package tddtrainer.catalog;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import tddtrainer.util.ThrowingFunction;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class KatalogLocator {

    private static final String DEFAULT_LOCATION = "https://bendisposto.github.io/dojo/";
    private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");

    private final KatalogPath root;

    public KatalogLocator() {
        this(KatalogPath.EMPTY);
    }

    public KatalogLocator(KatalogPath root) {
        this.root = root;
    }

    private static String getKatalogLocation() {
        return System.getProperty("katalog", DEFAULT_LOCATION);
    }

    private <T> T locateAndThen(KatalogPath katalogPath, ThrowingFunction<Path, T, IOException> pathFunction, ThrowingFunction<GetRequest, T, UnirestException> getFunction) {
        String location = getKatalogLocation();
        String path = root.append(katalogPath).getPath();

        try {
            Path p = Paths.get(location, path);
            try {
                return pathFunction.apply(p);
            } catch (IOException e) {
                throw new RuntimeException("Could not load Katalog via NIO from local path " + p, e);
            }
        } catch (InvalidPathException ignored) {
        }

        try {
            URI uri = new URI(location);
            uri = uri.resolve(new URI(path));

            String scheme = uri.getScheme();
            if (scheme != null) {
                try {
                    Path p = Paths.get(uri);
                    try {
                        return pathFunction.apply(p);
                    } catch (IOException e) {
                        throw new RuntimeException("Could not load Katalog via NIO from URI " + uri, e);
                    }
                } catch (FileSystemNotFoundException ignored) {
                }
            }

            try {
                GetRequest getRequest = Unirest.get(uri.toString());
                return getFunction.apply(getRequest);
            } catch (UnirestException e) {
                throw new RuntimeException("Could not load Katalog via GET from URI " + uri, e);
            }
        } catch (URISyntaxException ignored) {
        }

        throw new IllegalArgumentException("Could not load Katalog from unsupported path " + location + path);
    }

    public <T> T locateAndReadJson(KatalogPath katalogPath, Gson gson, Class<T> type) {
        Function<Reader, T> jsonParser = r -> gson.fromJson(r, type);
        return locateAndThen(
                katalogPath,
                p -> jsonParser.apply(Files.newBufferedReader(p)),
                g -> jsonParser.apply(new StringReader(g.asString().getBody()))
        );
    }

    public List<String> locateAndReadLines(KatalogPath katalogPath) {
        return locateAndThen(
                katalogPath,
                Files::readAllLines,
                g -> Arrays.asList(NEW_LINE_PATTERN.split(g.asString().getBody()))
        );
    }

    public KatalogLocator append(KatalogPath katalogPath) {
        return new KatalogLocator(root.append(katalogPath));
    }
}
