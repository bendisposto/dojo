package tddtrainer.events.babysteps;

public class Babysteps {

    public final boolean enabled;
    public final int time;

    public Babysteps(int time) {
        this.time = time;
        this.enabled = time > 0;
    }

}
