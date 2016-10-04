package tddtrainer.usermanager;

public class User {

    private String name;
    private String mail;

    public User(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }

    @Override
    public String toString() {
        return name + " (" + mail + ")";
    }

}
