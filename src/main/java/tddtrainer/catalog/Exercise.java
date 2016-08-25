package tddtrainer.catalog;

/**
 * Contains a java class with code and an associated test class
 * 
 * @author Marcel
 */
public class Exercise {

	private String name;
	private String description;
	private JavaClass code;
	private JavaClass test;
	private boolean babyStepsActivated;
	private int babyStepsCodeTime;
	private int babyStepsTestTime;

	public Exercise() {
		name = "";
		description = "";
		babyStepsCodeTime = 60;
		babyStepsTestTime = 60;
	}

	public Exercise(String name, String description) {
		this();
		this.name = name;
		this.description = description;
	}

	public Exercise(Exercise that) {
		this();
		this.name = that.name;
		this.description = that.description;
		this.code = that.code;
		this.test = that.test;
		this.babyStepsActivated = that.babyStepsActivated;
		this.babyStepsCodeTime = that.babyStepsCodeTime;
		this.babyStepsTestTime = that.babyStepsTestTime;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public JavaClass getCode() {
		return code;
	}

	public JavaClass getTest() {
		return test;
	}

	public boolean isBabyStepsActivated() {
		return babyStepsActivated;
	}

	public int getBabyStepsCodeTime() {
		return babyStepsCodeTime;
	}

	public int getBabyStepsTestTime() {
		return babyStepsTestTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCode(JavaClass code) {
		this.code = code;
	}

	public void setTest(JavaClass test) {
		this.test = test;
	}

	public void setBabyStepsActivated(boolean activated) {
		this.babyStepsActivated = activated;
	}

	public void setBabyStepsCodeTime(int babyStepsCodeTime) {
		this.babyStepsCodeTime = babyStepsCodeTime;
	}

	public void setBabyStepsTestTime(int babyStepsTestTime) {
		this.babyStepsTestTime = babyStepsTestTime;
	}

	@Override
	public String toString() {
		return String.format("{name=\"%s\" description=\"%s\" code=%s tests=%s}", name,
				description.replaceAll("\n", " "), code.toString(), test.toString());
	}

}
