package tddtrainer.usermanager;

public class NoSuchUserException extends Exception {

    private static final long serialVersionUID = 5736242258957390318L;
    private String mail;

    NoSuchUserException(String mail) {
        this.mail = mail;
    }

    @Override
    public String getMessage() {
        return "Username " + mail + " is not known or password was not correct.";
    }

}
