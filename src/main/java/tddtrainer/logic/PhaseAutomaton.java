package tddtrainer.logic;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import tddtrainer.events.ExerciseEvent;
import tddtrainer.gui.CompilationXResult;
import tddtrainer.logic.events.NextPhaseEvent;
import tddtrainer.logic.events.SwitchToGreenEvent;
import tddtrainer.logic.events.SwitchToRedEvent;
import tddtrainer.logic.events.SwitchToRefactorEvent;
import vk.core.api.CompilationUnit;
import vk.core.api.CompileError;
import vk.core.api.CompilerFactory;
import vk.core.api.CompilerResult;
import vk.core.api.JavaStringCompiler;
import vk.core.api.TestFailure;
import vk.core.api.TestResult;

public class PhaseAutomaton {

	private Phase currentState = Phase.RED;
	private EventBus bus;

	@Subscribe
	public void init(ExerciseEvent event) {
		CompilationUnit testCompilationUnit = event.getTestCU();
		CompilationUnit codeCompilationUnit = event.getCodeCU();
		JavaStringCompiler compiler = compile(testCompilationUnit, codeCompilationUnit);
		bus.post(new CompilationXResult(getConsoleText(compiler), true));
		currentState = Phase.RED;
		bus.post(new SwitchToRedEvent());
	}

	@Inject
	public PhaseAutomaton(EventBus bus) {
		this.bus = bus;
		bus.register(this);
	}

	@Subscribe
	public void next(NextPhaseEvent event) {

		CompilationUnit testCompilationUnit = event.getTestCU();
		CompilationUnit codeCompilationUnit = event.getCodeCU();

		JavaStringCompiler compiler = compile(testCompilationUnit, codeCompilationUnit);
		bus.post(compiler);
		switch (currentState) {
		case RED:
			switchToGreen(compiler, testCompilationUnit);
			break;
		case GREEN:
			switchToRefactor(compiler);
			break;
		case REFACTOR:
			switchToRed(compiler);
			break;
		}
	}

	private JavaStringCompiler compile(CompilationUnit testCompilationUnit, CompilationUnit codeCompilationUnit) {
		JavaStringCompiler compiler = CompilerFactory.getCompiler(
				codeCompilationUnit,
				testCompilationUnit);
		compiler.compileAndRunTests();
		return compiler;
	}

	private void switchToGreen(JavaStringCompiler compiler, CompilationUnit testCompilationUnit) {
		boolean proceed = aMethodIsMissing(compiler.getCompilerResult(), testCompilationUnit)
				|| aSingleTestFails(compiler.getTestResult());
		if (proceed) {
			currentState = Phase.GREEN;
			bus.post(new SwitchToGreenEvent());
		}
		bus.post(new CompilationXResult(getConsoleText(compiler), proceed));
	}

	private void switchToRefactor(JavaStringCompiler compiler) {
		boolean proceed = allClassesCompile(compiler) && allTestsGreen(compiler);
		if (proceed) {
			currentState = Phase.REFACTOR;
			bus.post(new SwitchToRefactorEvent());
		}
		bus.post(new CompilationXResult(getConsoleText(compiler), proceed));
	}

	private void switchToRed(JavaStringCompiler compiler) {
		boolean proceed = allClassesCompile(compiler) && allTestsGreen(compiler);
		if (proceed) {
			currentState = Phase.RED;
			bus.post(new SwitchToRedEvent());
		}
		bus.post(new CompilationXResult(getConsoleText(compiler), proceed));
	}

	private boolean aMethodIsMissing(CompilerResult compilerResult, CompilationUnit testCompilationUnit) {
		Collection<CompileError> testErrors = compilerResult.getCompilerErrorsForCompilationUnit(testCompilationUnit);
		if (moreThanOneCompileErrorInTest(testErrors))
			return false;
		for (CompileError compileError : testErrors) {
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

	private boolean allClassesCompile(JavaStringCompiler compiler) {
		return !compiler.getCompilerResult().hasCompileErrors();
	}

	private boolean aSingleTestFails(TestResult testResult) {
		return testResult.getNumberOfFailedTests() == 1;
	}

	private boolean allTestsGreen(JavaStringCompiler compiler) {
		return (compiler.getTestResult().getNumberOfFailedTests() == 0);
	}

	private String getConsoleText(JavaStringCompiler compiler) {
		CompilerResult compilerResult = compiler.getCompilerResult();
		Map<CompilationUnit, Collection<CompileError>> compilerErrors = compilerResult.getCompilerErrors();

		int errors = countErrors(compilerErrors.values());

		TestResult testResult = compiler.getTestResult();

		StringBuffer sb = new StringBuffer();
		sb.append("Compile Errors: ");
		sb.append(errors);
		sb.append("\n");
		appendCompilerErrorDetails(compilerErrors, sb);
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

	private void appendCompilerErrorDetails(Map<CompilationUnit, Collection<CompileError>> compilerErrors,
			StringBuffer sb) {
		Set<Entry<CompilationUnit, Collection<CompileError>>> entrySet = compilerErrors.entrySet();
		for (Entry<CompilationUnit, Collection<CompileError>> entry : entrySet) {
			sb.append("Class: ");
			sb.append(entry.getKey().getClassName());
			Collection<CompileError> errorsInCU = entry.getValue();
			sb.append(", Errors: ");
			sb.append(errorsInCU.size());
			sb.append("\n");
			for (CompileError compileError : errorsInCU) {
				sb.append("Line ");
				sb.append(compileError.getLineNumber());
				sb.append(": ");
				sb.append(compileError.getCodeLineContainingTheError().trim());
				sb.append("\n");
				sb.append(compileError.getMessage());
				sb.append("\n");
			}
		}
	}

}
