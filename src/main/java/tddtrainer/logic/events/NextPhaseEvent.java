package tddtrainer.logic.events;

import java.util.List;

import tddtrainer.catalog.JavaClass;

public class NextPhaseEvent {

	private final List<JavaClass> code;
	private final List<JavaClass> tests;

	public NextPhaseEvent(List<JavaClass> code, List<JavaClass> tests) {
		this.code = code;
		this.tests = tests;
	}

	public List<JavaClass> getCode() {
		return code;
	}

	public List<JavaClass> getTests() {
		return tests;
	}

}
