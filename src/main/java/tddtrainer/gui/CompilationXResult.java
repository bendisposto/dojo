package tddtrainer.gui;

public class CompilationXResult {

	private boolean proceed;
	private String consoleOutput;

	public CompilationXResult(String consoleOutput, boolean proceed) {
		this.consoleOutput = consoleOutput;
		this.proceed = proceed;
	}

	public boolean canProceed() {
		return proceed;
	}

	public String getConsoleOutput() {
		return consoleOutput;
	}

}
