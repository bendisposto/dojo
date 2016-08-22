package tddtrainer.events;

import java.util.ResourceBundle;

public class LanguageChangeEvent {
	private ResourceBundle bundle;
	
	public LanguageChangeEvent(ResourceBundle bundle) {
		this.bundle = bundle;
	}
	
	public ResourceBundle getBundle() {
		return bundle;
	}
}
