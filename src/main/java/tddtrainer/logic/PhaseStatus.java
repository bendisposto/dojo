package tddtrainer.logic;

import java.util.ArrayList;
import java.util.List;

import tddtrainer.executor.CompilationResult;
import tddtrainer.executor.ExecutionResult;
import vk.core.api.CompileError;
import vk.core.api.TestFailure;

/**
 * Contains, whether the {@link Phase} is valid for the current
 * {@link tddtrainer.catalog.Exercise} or not, the current {@link Phase} and
 * {@link ExecutionResult}.
 * 
 * @author Luisa
 *
 */
public class PhaseStatus {

	private boolean valid;
	private Phase phase;
	private ExecutionResult executionResult;

	public PhaseStatus(boolean valid, ExecutionResult executionResult, Phase phase) {
		this.valid = valid;
		this.executionResult = executionResult;
		this.phase = phase;
	}

	public boolean isValid() {
		return valid;
	}

	public Phase getPhase() {
		return phase;
	}

	void setExecutionResult(ExecutionResult executionResult) {
		this.executionResult = executionResult;
	}

	public String getExecutionResultAsString() {
		String executionResultAsString = "Compile Errors: ";
		if (executionResult.getCompilerResult().hasCompileErrors()) {
			List<CompilationResult> compilationResultsWithErrors = new ArrayList<>();
			for (CompilationResult cr : executionResult.getCompileErrors()) {
				if (!cr.getCompileErrors().isEmpty()) {
					compilationResultsWithErrors.add(cr);
				}
			}
			executionResultAsString += compilationResultsWithErrors.size() + "\n";
			for (CompilationResult cr : compilationResultsWithErrors) {
				executionResultAsString += "Class: " + cr.getClassName() + ", Errors: " + cr.getCompileErrors().size()
						+ "\n";
				for (CompileError ce : cr.getCompileErrors()) {
					executionResultAsString += "Line " + ce.getLineNumber() + ": "
							+ ce.getCodeLineContainingTheError().trim() + "\n" + ce.getMessage() + "\n";
				}
			}
		} else {
			executionResultAsString += "0\nSuccessful Tests: ";
			if (executionResult.getTestResult().getNumberOfFailedTests() != 0) {
				executionResultAsString += executionResult.getTestResult().getNumberOfSuccessfulTests()
						+ ", Failed Tests: " + executionResult.getTestResult().getNumberOfFailedTests() + "\n";
				for (TestFailure tf : executionResult.getTestResult().getTestFailures()) {
					executionResultAsString += "Class: " + tf.getTestClassName() + ", Method: " + tf.getMethodName()
							+ "\n" + tf.getMessage() + "\n";
				}
			} else {
				executionResultAsString += executionResult.getTestResult().getNumberOfSuccessfulTests()
						+ ", Failed Tests: 0\n";
			}
		}
		return executionResultAsString;
	}

}
