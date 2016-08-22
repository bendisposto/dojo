package tddtrainer.babysteps;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.google.common.eventbus.EventBus;

import javafx.application.Platform;
import tddtrainer.events.TimeEvent;
import tddtrainer.logic.PhaseManagerIF;

/**
 * An implementation of {@link BabystepsManagerIF} to force the user to make small tests and less code by limiting the time for editing code and tests.
 *
 */
public class BabystepsManager implements BabystepsManagerIF{
	
	PhaseManagerIF phaseManager;
	private boolean enabled = false;
	private boolean running = false;
	private LocalDateTime startTime;
	private LocalDateTime nowTime;
	private boolean stopped = true;
	private int phaseTime;
	private EventBus bus;
	
	public BabystepsManager(PhaseManagerIF phaseManager, EventBus bus) {
		this.phaseManager = phaseManager;
		this.bus = bus;
	}
	
	@Override
	public synchronized void start(int mPhaseTime) {
		if(this.enabled) {
			phaseTime = mPhaseTime;
			startTime = LocalDateTime.now();
			stopped = false;
			if(!running) {
				running = true;
				new Thread(() -> {
					while(!stopped) {
						nowTime = LocalDateTime.now();
						long dTime = startTime.until(nowTime, ChronoUnit.SECONDS);
		
			    		if(dTime > phaseTime) {
			    			Platform.runLater(() -> phaseManager.resetPhase());
							running = false; 
							return;
			    		};
						
			    		bus.post(new TimeEvent(phaseTime - dTime));
			    		
						try {
							Thread.sleep(1000);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					running = false;
				})
				.start();
			}
		}
	}
	
	public void stop() {
		if(this.enabled) {
			this.stopped = true;
		}
	}	
	public void enable(){
		this.enabled = true;
	}
	
	public void disable(){
		this.enabled = false;
	}
}
