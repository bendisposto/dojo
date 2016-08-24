package tddtrainer.logic;

import java.util.Collection;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import tddtrainer.events.ExerciseEvent;
import tddtrainer.logic.events.NextPhaseEvent;
import tddtrainer.logic.events.SwitchToGreenEvent;
import tddtrainer.logic.events.SwitchToRedEvent;
import tddtrainer.logic.events.SwitchToRefactorEvent;
import vk.core.api.CompilationUnit;
import vk.core.api.CompileError;
import vk.core.api.CompilerFactory;
import vk.core.api.CompilerResult;
import vk.core.api.JavaStringCompiler;
import vk.core.api.TestResult;

public class PhaseAutomaton {

	private Phase currentState = Phase.RED;
	private EventBus bus;

	@Subscribe
	public void init(ExerciseEvent exerciseEvent) {
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

		JavaStringCompiler compiler = CompilerFactory.getCompiler(
				codeCompilationUnit,
				testCompilationUnit);
		compiler.compileAndRunTests();
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

	private void switchToGreen(JavaStringCompiler compiler, CompilationUnit testCompilationUnit) {
		if (aMethodIsMissing(compiler.getCompilerResult(), testCompilationUnit)
				|| aSingleTestFails(compiler.getTestResult())) {
			currentState = Phase.GREEN;
			bus.post(new SwitchToGreenEvent());
		}
	}

	private void switchToRefactor(JavaStringCompiler compiler) {
		if (allClassesCompile(compiler) && allTestsGreen(compiler)) {
			currentState = Phase.REFACTOR;
			bus.post(new SwitchToRefactorEvent());
		}
	}

	private void switchToRed(JavaStringCompiler compiler) {
		if (allClassesCompile(compiler) && allTestsGreen(compiler)) {
			currentState = Phase.RED;
			bus.post(new SwitchToRedEvent());
		}
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
}
