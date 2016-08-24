package tddtrainer.logic.events;

import tddtrainer.catalog.Exercise;
import vk.core.api.CompilationUnit;

public class NextPhaseEvent {

	private Exercise exercise;
	private String code;
	private String test;

	public NextPhaseEvent(Exercise exercise, String code, String test) {
		this.exercise = exercise;
		this.code = code;
		this.test = test;
	}

	public NextPhaseEvent(Exercise exercise) {
		this.exercise = exercise;
		this.code = exercise.getCode().get(0).getCode();
		this.test = exercise.getTests().get(0).getCode();
	}

	public CompilationUnit getCodeCU() {
		String name = exercise.getCode().get(0).getName();
		return new CompilationUnit(name, code, false);
	}

	public CompilationUnit getTestCU() {
		String name = exercise.getTests().get(0).getName();
		return new CompilationUnit(name, test, true);
	}

}
