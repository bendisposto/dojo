package tddtrainer.catalog;

import java.lang.reflect.Type;
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

    private String defaultLocation = "https://www3.hhu.de/stups/downloads/dojo/katalog.json";
    private String location;

    static Gson gson = new Gson();

    public void setLocation(String location) {
        this.location = location;
    }

    public String fetchCatalog() throws UnirestException {
        String loc = location != null ? location : defaultLocation;
        GetRequest getRequest = Unirest.get(loc);
        HttpResponse<String> asString = getRequest.asString();
        return asString.getBody();
    }

    public List<Exercise> loadCatalog() throws JsonSyntaxException, UnirestException {
        Type collectionType = new TypeToken<Collection<Exercise>>() {
        }.getType();

        Collection<Exercise> es = gson.fromJson(fetchCatalog(), collectionType);
        ArrayList<Exercise> catalog = new ArrayList<>(es.size());
        catalog.addAll(es);
        return catalog;
    }

}
