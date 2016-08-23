package tddtrainer.logic;

import java.util.Collection;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import tddtrainer.catalog.JavaClass;
import vk.core.api.CompilationUnit;
import vk.core.api.CompileError;
import vk.core.api.CompilerFactory;
import vk.core.api.CompilerResult;
import vk.core.api.JavaStringCompiler;
import vk.core.api.TestResult;

public class PhaseAutomaton {

	private Phase currentState = Phase.RED;
	private EventBus bus;

	public void init() {
		currentState = Phase.RED;
	}

	@Inject
	public PhaseAutomaton(EventBus bus) {
		this.bus = bus;
		bus.register(this);
	}

	@Subscribe
	public void next(NextPhaseEvent event) {
		JavaClass code = event.getCode().get(0);
		JavaClass test = event.getTests().get(0);
		CompilationUnit testCompilationUnit = new CompilationUnit(test.getName(), test.getCode(), true);
		CompilationUnit codeCompilationUnit = new CompilationUnit(code.getName(), code.getCode(), false);
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
