package tddtrainer.logic;

import static org.hamcrest.core.StringContains.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.eventbus.EventBus;

import tddtrainer.catalog.FakeCatalogDatasource;
import tddtrainer.gui.catalog.ExerciseSelector;
import tddtrainer.tracking.TrackingManager;

public class PhaseStatusTest {

	@Test
	public void testWithNoCompileErrorsAndNoFailingTests() {
		EventBus bus = new EventBus();
		PhaseStatus phaseStatus = new PhaseManager(new TrackingManager(bus),
				new ExerciseSelector(new FakeCatalogDatasource(), null), bus)
						.checkPhase(new FakeCatalogDatasource().loadCatalog().get(0), true);
		assertEquals("Compile Errors: 0\nSuccessful Tests: 1, Failed Tests: 0\n",
				phaseStatus.getExecutionResultAsString());
	}

	@Test
	public void testWithCompileErrors() {
		EventBus bus = new EventBus();
		PhaseStatus phaseStatus = new PhaseManager(new TrackingManager(bus),
				new ExerciseSelector(new FakeCatalogDatasource(), null), bus)
						.checkPhase(new FakeCatalogDatasource().loadCatalog().get(1), true);

		String result = phaseStatus.getExecutionResultAsString();
		assertThat(result, containsString("Compile Errors: 1\nClass: CompileErrorTest, Errors: 1\nLine"));
		assertThat(result, containsString(
				": assertEquals(2, c.returnTwo());\ncannot find symbol\n  symbol:   method returnTwo()\n  location: variable c of type"));
		assertThat(result, containsString("CompileErrorCode"));
	}

	@Test
	public void testWithNoCompileErrorsAndOneFailingTest() {
		EventBus bus = new EventBus();
		PhaseStatus phaseStatus = new PhaseManager(new TrackingManager(bus),
				new ExerciseSelector(new FakeCatalogDatasource(), null), bus)
						.checkPhase(new FakeCatalogDatasource().loadCatalog().get(2), true);
		String result = phaseStatus.getExecutionResultAsString();
		assertThat(result, containsString("Compile Errors: 0\nSuccessful Tests: 0, Failed Tests: 1\nClass:"));
		assertThat(result, containsString("TestErrorTest, Method: testCode\nexpected:<1> but was:<2>"));
	}

}
