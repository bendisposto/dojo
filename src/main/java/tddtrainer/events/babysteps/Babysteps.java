package tddtrainer.events.babysteps;

public class Babysteps {

	public final boolean enabled;

	public final static Babysteps ON = new Babysteps(true);
	public final static Babysteps OFF = new Babysteps(false);

	private Babysteps(boolean on) {
		this.enabled = on;
	}

}
