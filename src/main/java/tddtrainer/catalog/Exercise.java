package tddtrainer.catalog;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a java class with code and an associated test class
 * 
 * @author Marcel
 */
public class Exercise {

	private String name;
	private String description;
	private List<JavaClass> code;
	private List<JavaClass> tests;
	private boolean babyStepsActivated;
	private int babyStepsCodeTime;
	private int babyStepsTestTime;

	public Exercise() {
		name = "";
		description = "";
		babyStepsCodeTime = 60;
		babyStepsTestTime = 60;
		code = new ArrayList<>();
		tests = new ArrayList<>();
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
		this.code.addAll(that.code);
		this.tests.addAll(that.tests);
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

	public List<JavaClass> getCode() {
		return code;
	}

	public List<JavaClass> getTests() {
		return tests;
	}

	public JavaClass getCode(int index) {
		if (index >= code.size())
			return null;
		return code.get(index);
	}

	public JavaClass getTest(int index) {
		if (index >= tests.size())
			return null;
		return tests.get(index);
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

	public void addCode(JavaClass code) {
		this.code.add(code);
	}

	public void addTest(JavaClass test) {
		this.tests.add(test);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCode(List<JavaClass> code) {
		this.code = code;
	}

	public void setTests(List<JavaClass> test) {
		this.tests = test;
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
				description.replaceAll("\n", " "), code.toString(), tests.toString());
	}

}
