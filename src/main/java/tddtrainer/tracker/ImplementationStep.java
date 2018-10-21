package tddtrainer.tracker;

import tddtrainer.automaton.Phase;
import vk.core.api.CompilationUnit;

import java.time.Duration;

public class ImplementationStep extends AbstractStep {

    private String code;
    private String test;
    private String compilerOutput;
    private String consoleOutput;

    public ImplementationStep(Phase p, Duration d, CompilationUnit code, CompilationUnit test, String compilerOutput,
                              String consoleOutput) {
        super(p, d);
        this.code = code.getClassContent();
        this.test = test.getClassContent();
        this.compilerOutput = compilerOutput;
        this.consoleOutput = consoleOutput;
    }

    public String getCode() {
        return code;
    }

    public String getCompilerOutputs() {
        return compilerOutput;
    }

    public String getTest() {
        return test;
    }

    public String getConsoleOutputs() {
        return consoleOutput;
    }

    @Override
    public String toString() {
        return "IMPLEMENTATION " + code + " " + test;
    }

}
