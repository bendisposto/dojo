package tddtrainer.automaton;

/**
 * This class is an event, that is triggered when you can proceed to another {@link Phase}.
 */
public class CanProceedEvent {

    private final boolean proceed;

    public CanProceedEvent(boolean proceed) {
        this.proceed = proceed;
    }

    public boolean canProceed() {
        return proceed;
    }

}
