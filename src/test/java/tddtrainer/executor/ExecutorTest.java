package tddtrainer.executor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tddtrainer.catalog.*;
import tddtrainer.executor.ExecutionResult;
import tddtrainer.executor.Executor;

public class ExecutorTest {
	
	@Test
	public void testExecuteWithOneFailingTest() {
		JavaClass code = new JavaClass("Main",
			"public class Main {\n"
			+ "public static void main(String[] args) {\n"
			+ "String s = ausgabe(\"Hallo Welt\");\n"
			+ "}\n"
			+ "public static String ausgabe(String string){\n"
			+ "return string + \"!\";\n"
			+ "}\n"
			+ "}\n");
		JavaClass test = new JavaClass("JunitTest",
			"import static org.junit.Assert.*;\n"
			+ "import org.junit.Test;\n"
			+ "public class JunitTest {\n"
			+ "@Test\n"
			+ "public void testMain() {\n"
			+ "assertEquals(\"Hallo abc!\", Main.ausgabe(\"Hallo Welt\"));\n"
			+ "}\n"
			+ "}\n");
		Exercise exercise = new Exercise();
		exercise.addCode(code);
		exercise.addTest(test);
		
		ExecutionResult result = new Executor().execute(exercise);
		assertEquals(false, result.getCompilerResult().hasCompileErrors());
		assertEquals(1, result.getTestResult().getNumberOfFailedTests());
	}
	
	@Test
	public void testExecuteWithNoFailingTest() {
		JavaClass code = new JavaClass("Main",
			"public class Main {\n"
			+ "public static void main(String[] args) {\n"
			+ "String s = ausgabe(\"Hallo Welt\");\n"
			+ "}\n"
			+ "public static String ausgabe(String string){\n"
			+ "return string + \"!\";\n"
			+ "}\n"
			+ "}\n");
		JavaClass test = new JavaClass("JunitTest",
			"import static org.junit.Assert.*;\n"
			+ "import org.junit.Test;\n"
			+ "public class JunitTest {\n"
			+ "@Test\n"
			+ "public void testMain() {\n"
			+ "assertEquals(\"Hallo Welt!\", Main.ausgabe(\"Hallo Welt\"));\n"
			+ "}\n"
			+ "}\n");
		Exercise exercise = new Exercise();
		exercise.addCode(code);
		exercise.addTest(test);
		
		ExecutionResult result = new Executor().execute(exercise);
		assertEquals(false, result.getCompilerResult().hasCompileErrors());
		assertEquals(1, result.getTestResult().getNumberOfSuccessfulTests());
	}
	
	@Test
	public void testExecuteWithCompileError() {
		JavaClass code = new JavaClass("Main",
			"public class Main {\n"
			+ "public static void main(String[] args) {\n"
			+ "String s = ausgabe(\"Hallo Welt\");\n"
			+ "}\n"
			+ "public static String ausgabe(String string){\n"
			+ "return string + \"!\";\n"
			+ "}\n");
		JavaClass test = new JavaClass("JunitTest",
			"import static org.junit.Assert.*;\n"
			+ "import org.junit.Test;\n"
			+ "public class JunitTest {\n"
			+ "@Test\n"
			+ "public void testMain() {\n"
			+ "assertEquals(\"Hallo Welt!\", Main.ausgabe(\"Hallo Welt\"));\n"
			+ "}\n"
			+ "}\n");
		Exercise exercise = new Exercise();
		exercise.addCode(code);
		exercise.addTest(test);
		
		ExecutionResult result = new Executor().execute(exercise);
		assertEquals(true, result.getCompilerResult().hasCompileErrors());
	}
	
	@Test
	public void testExecuteWithExerciseFromCatalogNoError() {
		CatalogDatasourceIF catalogDs = new FakeCatalogDatasource();
		
		Exercise exercise = catalogDs.loadCatalog().get(0);
		
		ExecutionResult result = new Executor().execute(exercise);
		assertEquals(false, result.getCompilerResult().hasCompileErrors());
		assertEquals(1, result.getTestResult().getNumberOfSuccessfulTests());
	}
	
	
	@Test
	public void testExecuteWithExerciseFromCatalogCompileError() {
		CatalogDatasourceIF catalogDs = new FakeCatalogDatasource();
		
		Exercise exercise = catalogDs.loadCatalog().get(1);
		
		ExecutionResult result = new Executor().execute(exercise);
		assertEquals(true, result.getCompilerResult().hasCompileErrors());
	}
	
	
	@Test
	public void testExecuteWithExerciseFromCatalogTestError() {
		CatalogDatasourceIF catalogDs = new FakeCatalogDatasource();
		
		Exercise exercise = catalogDs.loadCatalog().get(2);
		
		ExecutionResult result = new Executor().execute(exercise);
		assertEquals(false, result.getCompilerResult().hasCompileErrors());
		assertEquals(1, result.getTestResult().getNumberOfFailedTests());
	}
	
	@Test
	public void testExecuteWithCompileErrorMessageNameOfClass() {
		JavaClass code = new JavaClass("Main",
				"public class Main {\n"
				+ "public static void main(String[] args) {\n"
				+ "String s = ausgabe(\"Hallo Welt\");\n"
				+ "}\n"
				+ "public static String ausgabe(String string){\n"
				+ "return string + \"!\";\n"
				+ "}\n");
			JavaClass test = new JavaClass("JunitTest",
				"import static org.junit.Assert.*;\n"
				+ "import org.junit.Test;\n"
				+ "public class JunitTest {\n"
				+ "@Test\n"
				+ "public void testMain() {\n"
				+ "assertEquals(\"Hallo Welt!\", Main.ausgabe(\"Hallo Welt\"));\n"
				+ "}\n"
				+ "}\n");
			Exercise exercise = new Exercise();
			exercise.addCode(code);
			exercise.addTest(test);
			
			ExecutionResult result = new Executor().execute(exercise);
			assertEquals("Main", result.getCompileErrors().get(0).getClassName());
	}
	
	@Test
	public void testExecuteWithCompileErrorMessageError() {
		JavaClass code = new JavaClass("Main",
				"public class Main {\n"
				+ "public static void main(String[] args) {\n"
				+ "String s = ausgabe(\"Hallo Welt\");\n"
				+ "}\n"
				+ "public static String ausgabe(String string){\n"
				+ "return string + \"!\";\n"
				+ "}\n");
			JavaClass test = new JavaClass("JunitTest",
				"import static org.junit.Assert.*;\n"
				+ "import org.junit.Test;\n"
				+ "public class JunitTest {\n"
				+ "@Test\n"
				+ "public void testMain() {\n"
				+ "assertEquals(\"Hallo Welt!\", Main.ausgabe(\"Hallo Welt\"));\n"
				+ "}\n"
				+ "}\n");
			Exercise exercise = new Exercise();
			exercise.addCode(code);
			exercise.addTest(test);
			
			ExecutionResult result = new Executor().execute(exercise);
			assertEquals("reached end of file while parsing", result.getCompileErrors().get(0).getCompileErrors().get(0).getMessage());
	}
	
}
