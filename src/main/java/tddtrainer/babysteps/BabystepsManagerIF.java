package tddtrainer.babysteps;

public interface BabystepsManagerIF {
	
	/**
	 * 
	 * arguments = seconds
	 * enables/disables the Babysteps
	 * @param mPhaseTime The time until the {@link tddtrainer.catalog.Exercise} gets reset.
	 */
	public void start(int mPhaseTime);
	
	
}
