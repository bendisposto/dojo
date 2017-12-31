package tddtrainer.events;

public class JavaCodeChangeEvent {

    public static enum CodeType {
        TEST, CODE;
    }

    private String text;
    private CodeType type;

    public JavaCodeChangeEvent(String text, CodeType type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public CodeType getType() {
        return type;
    }

}
