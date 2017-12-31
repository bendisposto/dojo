package tddtrainer.automaton;

public class CanProceedEvent {

    private final boolean proceed;

    public CanProceedEvent(boolean proceed) {
        this.proceed = proceed;
    }

    public boolean canProceed() {
        return proceed;
    }

}
