package tddtrainer.babysteps;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import javafx.application.Platform;
import tddtrainer.events.TimeEvent;
import tddtrainer.events.automaton.ResetPhaseEvent;
import tddtrainer.events.automaton.SwitchedToGreenEvent;
import tddtrainer.events.automaton.SwitchedToRedEvent;
import tddtrainer.events.automaton.SwitchedToRefactorEvent;
import tddtrainer.events.babysteps.Babysteps;

/**
 * An implementation of {@link BabystepsManagerIF} to force the user to make
 * small tests and less code by limiting the time for editing code and tests.
 *
 */
public class BabystepsManager {

	private boolean enabled = false;
	private boolean running = false;
	private LocalDateTime startTime;
	private LocalDateTime nowTime;
	private boolean stopped = true;
	private int phaseTime;
	private EventBus bus;

	@Inject
	public BabystepsManager(EventBus bus) {
		this.bus = bus;
		bus.register(this);
	}

	@Subscribe
	private void enterRed(SwitchedToRedEvent e) {
		start();
	}

	@Subscribe
	private void enterRed(SwitchedToGreenEvent e) {
		start();
	}

	@Subscribe
	private void enterRed(SwitchedToRefactorEvent e) {
		stopped = true;
	}

	private synchronized void start() {
		if (this.enabled) {
			startTime = LocalDateTime.now();
			stopped = false;
			if (!running) {
				running = true;
				new Thread(() -> {
					while (!stopped) {
						nowTime = LocalDateTime.now();
						long dTime = startTime.until(nowTime, ChronoUnit.SECONDS);

						if (dTime > phaseTime) {
							Platform.runLater(() -> bus.post(new ResetPhaseEvent()));
							running = false;
							return;
						}

						bus.post(new TimeEvent(phaseTime - dTime));

						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					running = false;
				})
						.start();
			}
		}
	}

	@Subscribe
	public void switchBabysteps(Babysteps event) {
		this.enabled = event.enabled;
		this.phaseTime = event.time;
	}
}
