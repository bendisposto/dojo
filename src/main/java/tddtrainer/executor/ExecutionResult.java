package tddtrainer.executor;

import java.util.*;

/*
 * Saves the result of the execution of the program and tests.
 * The list compileErrors contains a list of strings in which the first element is the name of the class which has failed to
 * compile and the other elements are the error messages.
 */

import vk.core.api.*;

/**
 * Contains the Execution Result, i.e. the Compiler Result, the Test Result and a list of Compile Errors.
 * @author Luisa
 *
 */
public class ExecutionResult {

	private CompilerResult compilerResult;
	private TestResult testResult;
	private List<CompilationResult> compileErrors = new ArrayList<>();

	/**
	 * Contructor to call, if compile errors have occurred.
	 * @param cr The {@link CompilerResult} of the Execution
	 * @param fcu The list of {@link CompilationUnit CompilationUnits}
	 */
	public ExecutionResult(CompilerResult cr, List<CompilationUnit> fcu) {
		compilerResult = cr;
		for(CompilationUnit cu : fcu) {
			compileErrors.add(new CompilationResult(cu.getClassName(), new ArrayList<>(cr.getCompilerErrorsForCompilationUnit(cu))));
		}
	}
	
	/**
	 * Constructor to call, if no compile errors have occurred.
	 * @param cr The current {@link CompilerResult}
	 * @param tr The current {@link TestResult}
	 */
	public ExecutionResult(CompilerResult cr, TestResult tr) {
		compilerResult = cr;
		testResult = tr;
	}
	
	public CompilerResult getCompilerResult() {
		return compilerResult;
	}
	
	public TestResult getTestResult() {
		return testResult;
	}
	
	public List<CompilationResult> getCompileErrors() {
		return compileErrors;
	}
	
}
