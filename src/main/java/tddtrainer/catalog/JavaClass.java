package tddtrainer.catalog;

import java.util.List;

public class JavaClass {
    private String name;
    private String[] code;

    public JavaClass(String name, List<String> lines) {
        this.name = name;
        this.code = lines.toArray(new String[0]);
    }

    public JavaClass(String name, String code) {
        this.name = name;
        this.code = code.split("\n");
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return String.join("\n", code);
    }

    @Override
    public String toString() {
        return String.format("{name=\"%s\" code=\"%s\"}", name, String.join(" ", code));
    }

}
