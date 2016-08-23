package tddtrainer.logic;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

import tddtrainer.catalog.FakeCatalogDatasource;
import tddtrainer.gui.catalog.ExerciseSelector;
import tddtrainer.tracking.TrackingManager;

public class PhaseStatusTest {

	@Test
	public void testWithNoCompileErrorsAndNoFailingTests() {
		PhaseStatus phaseStatus = new PhaseManager(new TrackingManager(),
				new ExerciseSelector(new FakeCatalogDatasource(), null), new EventBus())
						.checkPhase(new FakeCatalogDatasource().loadCatalog().get(0), true);
		assertEquals("Compile Errors: 0\nSuccessful Tests: 1, Failed Tests: 0\n",
				phaseStatus.getExecutionResultAsString());
	}

	@Ignore("assert too fragile")
	@Test
	// FIXME This test needs some love
	public void testWithCompileErrors() {
		PhaseStatus phaseStatus = new PhaseManager(new TrackingManager(),
				new ExerciseSelector(new FakeCatalogDatasource(), null), new EventBus())
						.checkPhase(new FakeCatalogDatasource().loadCatalog().get(1), true);
		assertEquals(
				"Compile Errors: 1\nClass: CompileErrorTest, Errors: 1\nLine 9: assertEquals(2, c.returnTwo());\ncannot find symbol\n  symbol:   method returnTwo()\n  location: variable c of type CompileErrorCode\n",
				phaseStatus.getExecutionResultAsString());
	}

	@Ignore("assert too fragile")
	@Test
	// FIXME This test needs some love
	public void testWithNoCompileErrorsAndOneFailingTest() {
		PhaseStatus phaseStatus = new PhaseManager(new TrackingManager(),
				new ExerciseSelector(new FakeCatalogDatasource(), null), new EventBus())
						.checkPhase(new FakeCatalogDatasource().loadCatalog().get(2), true);
		assertEquals(
				"Compile Errors: 0\nSuccessful Tests: 0, Failed Tests: 1\nClass: TestErrorTest, Method: testCode\nexpected:<1> but was:<2>\n",
				phaseStatus.getExecutionResultAsString());
	}

}
