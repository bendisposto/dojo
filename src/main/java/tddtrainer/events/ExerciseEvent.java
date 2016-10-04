package tddtrainer.events;

import tddtrainer.catalog.Exercise;
import vk.core.api.CompilationUnit;

public class ExerciseEvent {

    private Exercise exercise;
    private String code;
    private String test;

    public ExerciseEvent(Exercise exercise) {
        this.exercise = exercise;
        this.code = exercise.getCode().getCode();
        this.test = exercise.getTest().getCode();
    }

    public Exercise getExercise() {
        return exercise;
    }

    public CompilationUnit getCodeCU() {
        String name = exercise.getCode().getName();
        return new CompilationUnit(name, code, false);
    }

    public CompilationUnit getTestCU() {
        String name = exercise.getTest().getName();
        return new CompilationUnit(name, test, true);
    }

}
