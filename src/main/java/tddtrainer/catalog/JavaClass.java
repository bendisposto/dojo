package tddtrainer.catalog;

/**
 * Represents a Java class with a class name and its code
 * 
 * @author Marcel
 */
public class JavaClass {
	private String name;
	private String code;

	public JavaClass(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return String.format("{name=\"%s\" code=\"%s\"}", name, code.replaceAll("\n", " "));
	}

}
