package tddtrainer.compiler;

import tddtrainer.catalog.Exercise;
import vk.core.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class AutoCompilerResult {

    private final Collection<CompileError> errorsInTest;
    private final TestResult testResult;
    private final boolean hasCompileErrors;
    private final boolean hasFailingTests;
    private final String compilerOutput;
    private final String testOutput;
    private final CompilationUnit testCU;
    private final CompilationUnit codeCU;
    private Collection<CompileError> allErrors;

    AutoCompilerResult(JavaStringCompiler compiler, Exercise exercise) {
        testCU = compiler.getCompilationUnitByName(exercise.getTest().getName());
        codeCU = compiler.getCompilationUnitByName(exercise.getCode().getName());

        allErrors = new ArrayList<>();
        allErrors.addAll(compiler.getCompilerResult().getCompilerErrors());
        allErrors.addAll(compiler.getCompilerResult().getStyleErrors());
        errorsInTest = allErrors.stream().filter(e -> e.getCompilationUnit().isATest()).collect(Collectors.toList());

        testResult = compiler.getTestResult();
        hasCompileErrors = compiler.getCompilerResult().hasCompileErrors();
        hasFailingTests = !hasCompileErrors && compiler.getTestResult().getNumberOfFailedTests() != 0;
        compilerOutput = getConsoleText(compiler, testCU, codeCU);
        testOutput = testResult == null ? "" : testResult.getOutput();
    }

    public String getCompilerOutput() {
        return compilerOutput;
    }

    public String getTestOutput() {
        return testOutput;
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
        return testResult != null && testResult.getNumberOfFailedTests() == 1;
    }

    public boolean allClassesCompile() {
        return !hasCompileErrors;
    }

    public boolean allTestsGreen() {
        return !hasFailingTests;
    }

    private String getConsoleText(JavaStringCompiler compiler, CompilationUnit testCU, CompilationUnit codeCU) {

        int errors = allErrors.size();

        TestResult testResult = compiler.getTestResult();

        StringBuffer sb = new StringBuffer();
        sb.append("Compile Errors: ");
        sb.append(errors);
        sb.append("\n");
        for (CompileError error : allErrors) {
            sb.append(error.toString());
            sb.append("\n");
        }

        if (errors == 0) {
            appendTestResults(testResult, sb);
        }
        return sb.toString();
    }

    /**
     * In most cases a small getMessage returns enough information to debug the error. But some exceptions require more
     * information. In this case a part of the StackTrace is appended to the StringBuffer.
     *
     * @param sb
     * @param failure
     */
    private void stackTraceOrMessage(StringBuffer sb, TestFailure failure) {
        String[] linesOfStackTrace = failure.getExceptionStackTrace().split("\n");
        if (linesOfStackTrace[0].contains("ArrayIndexOutOfBoundsException")) {
            String limitedStackTrace = Arrays.stream(linesOfStackTrace).limit(5)
                    .collect(Collectors.joining("\n")) + "\n";
            sb.append(limitedStackTrace);
            if (linesOfStackTrace.length > 5)
                sb.append("\t...\n");
        } else {
            sb.append(failure.getMessage());
        }
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
                stackTraceOrMessage(sb, failure);
                sb.append("\n");
            }
        }
    }

    public CompilationUnit getCodeCU() {
        return codeCU;
    }

    public CompilationUnit getTestCU() {
        return testCU;
    }

}
