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

public class KatalogLocatorService {

    private static final String DEFAULT_LOCATION = "https://bendisposto.github.io/dojo/";
    private static final Pattern NEW_LINE_PATTERN = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");

    private final KatalogLocator root;

    public KatalogLocatorService() {
        this(KatalogLocator.EMPTY);
    }

    public KatalogLocatorService(KatalogLocator root) {
        this.root = root;
    }

    private static String getKatalogLocation() {
        return System.getProperty("katalog", DEFAULT_LOCATION);
    }

    private <T> T locateAndThen(KatalogLocator locator, ThrowingFunction<Path, T, IOException> pathFunction, ThrowingFunction<GetRequest, T, UnirestException> getFunction) {
        String katalogLocation = getKatalogLocation();
        String path = root.append(locator).getPath();

        try {
            Path p = Paths.get(katalogLocation, path);
            try {
                return pathFunction.apply(p);
            } catch (IOException e) {
                throw new RuntimeException("Could not load Katalog via NIO from local path " + p, e);
            }
        } catch (InvalidPathException ignored) {
        }

        try {
            URI uri = new URI(katalogLocation);

            String scheme = uri.getScheme();
            if (scheme != null) {
                uri = uri.resolve(path);
                try {
                    Path p = Paths.get(uri);
                    try {
                        return pathFunction.apply(p);
                    } catch (IOException e) {
                        throw new RuntimeException("Could not load Katalog as URI via NIO from " + uri, e);
                    }
                } catch (FileSystemNotFoundException ignored) {
                }

                try {
                    GetRequest getRequest = Unirest.get(katalogLocation);
                    return getFunction.apply(getRequest);
                } catch (UnirestException e) {
                    throw new RuntimeException("Could not load Katalog via GET from " + uri, e);
                }
            }
        } catch (URISyntaxException ignored) {
        }

        throw new IllegalArgumentException("Could not load Katalog from unsupported locator " + katalogLocation);
    }

    public <T> T locateAndReadJson(KatalogLocator locator, Gson gson, Class<T> type) {
        Function<Reader, T> jsonParser = r -> gson.fromJson(r, type);
        return locateAndThen(
                locator,
                p -> jsonParser.apply(Files.newBufferedReader(p)),
                g -> jsonParser.apply(new StringReader(g.asString().getBody()))
        );
    }

    public List<String> locateAndReadLines(KatalogLocator locator) {
        return locateAndThen(
                locator,
                Files::readAllLines,
                g -> Arrays.asList(NEW_LINE_PATTERN.split(g.asString().getBody()))
        );
    }

    public KatalogLocatorService append(KatalogLocator locator) {
        return new KatalogLocatorService(root.append(locator));
    }
}
