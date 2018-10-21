package tddtrainer.events;

public class JavaCodeChangeEvent {

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

    public enum CodeType {
        TEST, CODE
    }

}
