package tddtrainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;

import tddtrainer.catalog.CatalogDatasourceIF;
import tddtrainer.catalog.XMLCatalogDatasource;
import tddtrainer.gui.catalog.ExerciseSelector;
import tddtrainer.logic.PhaseManager;
import tddtrainer.tracking.TrackingManager;

public class GuiceConfiguration extends AbstractModule {

	private final Logger logger = LoggerFactory.getLogger(GuiceConfiguration.class);

	@Override
	protected void configure() {
		logger.trace("Configuring Dependency Injection");
		bind(EventBus.class).asEagerSingleton();
		bind(PhaseManager.class).asEagerSingleton();
		bind(TrackingManager.class);
		bind(ExerciseSelector.class).asEagerSingleton();
		bind(CatalogDatasourceIF.class).to(XMLCatalogDatasource.class);
		logger.trace("Configuring Dependency Injection completed");
	}

}
