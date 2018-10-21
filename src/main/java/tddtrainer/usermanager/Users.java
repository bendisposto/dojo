package tddtrainer.usermanager;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class Users {

    private final Set<User> currentUsers = new HashSet<>();

    public void loginAuasUser(String mail, String passwordText) throws NoSuchUserException, UnirestException {
        Document document = requestPageFromAuas(mail, passwordText);
        if (isAuasUser(document)) {
            String user = fetchUserNameFromAuas(mail, document);
            currentUsers.add(new User(user, mail));
        } else {
            throw new NoSuchUserException(mail);
        }
    }

    public void loginNonAuasUser(String name, String mail) {
        currentUsers.add(new User(name, mail));
    }

    public void logoutUser(User user) {
        currentUsers.remove(user);
    }

    public Set<User> getCurrentUsers() {
        return currentUsers;
    }

    private String fetchUserNameFromAuas(String mail, Document document) {
        String user = mail;
        Elements nameTag = document.select("h1");
        if (nameTag.size() == 2) {
            String welcomeMessage = nameTag.get(1).html();
            welcomeMessage = welcomeMessage.replace("Willkommen ", "");
            welcomeMessage = welcomeMessage.replaceAll("!$", "");
            user = welcomeMessage;
        }
        return user;
    }

    private boolean isAuasUser(Document document) {
        return document.select(".errormessage ul li").isEmpty();
    }

    private Document requestPageFromAuas(String mail, String passwordText) throws UnirestException {
        HttpResponse<String> response = Unirest.post("https://auas.cs.uni-duesseldorf.de/abgabe/student/login")
                .field("LoginEmail", mail)
                .field("LoginPassword", passwordText).asString();
        if (response.getStatus() == 200) {
            response = Unirest.get("https://auas.cs.uni-duesseldorf.de/abgabe/student/index")
                    .asString();
        }
        String body = response.getBody();
        return Jsoup.parse(body);
    }

}
