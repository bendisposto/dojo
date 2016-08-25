package tddtrainer.compiler;

import java.util.Collection;
import java.util.Map;

import tddtrainer.catalog.Exercise;
import vk.core.api.CompilationUnit;
import vk.core.api.CompileError;
import vk.core.api.CompilerResult;
import vk.core.api.JavaStringCompiler;
import vk.core.api.TestFailure;
import vk.core.api.TestResult;

public class AutoCompilerResult {

	private final Collection<CompileError> errorsInTest;
	private final TestResult testResult;
	private final boolean hasCompileErrors;
	private final boolean hasFailingTests;
	private final String compilerOutput;

	public AutoCompilerResult(JavaStringCompiler compiler, Exercise exercise) {
		CompilationUnit testCU = compiler.getCompilationUnitByName(exercise.getTest().getName());
		CompilationUnit codeCU = compiler.getCompilationUnitByName(exercise.getCode().getName());
		errorsInTest = compiler.getCompilerResult().getCompilerErrorsForCompilationUnit(testCU);
		testResult = compiler.getTestResult();
		hasCompileErrors = compiler.getCompilerResult().hasCompileErrors();
		hasFailingTests = hasCompileErrors ? false : compiler.getTestResult().getNumberOfFailedTests() != 0;
		compilerOutput = getConsoleText(compiler, testCU, codeCU);
	}

	public String getCompilerOutput() {
		return compilerOutput;
	}

	public boolean aMethodIsMissing() {
		if (moreThanOneCompileErrorInTest(errorsInTest))
			return false;
		for (CompileError compileError : errorsInTest) {
			if (!isMissingSymbol(compileError)) {
				return false;
			}
		}
		return true;
	}

	private boolean moreThanOneCompileErrorInTest(Collection<CompileError> testErrors) {
		return testErrors.size() != 1;
	}

	private boolean isMissingSymbol(CompileError ce) {
		return ce.getMessage().contains("cannot find symbol");
	}

	public boolean aSingleTestFails() {
		return testResult.getNumberOfFailedTests() == 1;
	}

	public boolean allClassesCompile() {
		return !hasCompileErrors;
	}

	public boolean allTestsGreen() {
		return !hasFailingTests;
	}

	private String getConsoleText(JavaStringCompiler compiler, CompilationUnit testCU, CompilationUnit codeCU) {

		CompilerResult compilerResult = compiler.getCompilerResult();
		Map<CompilationUnit, Collection<CompileError>> compilerErrors = compilerResult.getCompilerErrors();

		int errors = countErrors(compilerErrors.values());

		TestResult testResult = compiler.getTestResult();

		StringBuffer sb = new StringBuffer();
		sb.append("Compile Errors: ");
		sb.append(errors);
		sb.append("\n");
		for (CompileError error : errorsInTest) {
			sb.append(error.toString());
			sb.append("\n");
		}

		if (errors == 0) {
			appendTestResults(testResult, sb);
		}
		return sb.toString();
	}

	private int countErrors(Collection<Collection<CompileError>> values) {
		int sum = 0;
		for (Collection<CompileError> v : values) {
			sum += v.size();
		}
		return sum;
	}

	private void appendTestResults(TestResult testResult, StringBuffer sb) {
		sb.append("Successful Tests: ");
		sb.append(testResult.getNumberOfSuccessfulTests());
		sb.append("\n");
		sb.append("Ignored Tests: ");
		sb.append(testResult.getNumberOfIgnoredTests());
		sb.append("\n");
		sb.append("Failed Tests: ");
		sb.append(testResult.getNumberOfFailedTests());
		sb.append("\n");
		if (testResult.getNumberOfFailedTests() > 0) {
			sb.append("Failures:\n");
			Collection<TestFailure> failures = testResult.getTestFailures();
			for (TestFailure failure : failures) {
				sb.append("Class: ");
				sb.append(failure.getTestClassName());
				sb.append(", Method: ");
				sb.append(failure.getMethodName());
				sb.append("\n");
				sb.append(failure.getMessage());
				sb.append("\n");
			}
		}
	}

}
