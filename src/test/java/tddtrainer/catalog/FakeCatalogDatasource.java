package tddtrainer.catalog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A dummy {@link CatalogDatasourceIF} implementation which contains three
 * exercises where the first has no errors, the second has a compile error and
 * the third has a test error.
 * 
 * @author Marcel
 *
 */
public class FakeCatalogDatasource implements CatalogDatasourceIF {

	@Override
	public List<Exercise> loadCatalog() {
		ArrayList<Exercise> catalog = new ArrayList<>();
		Exercise working = new Exercise("Working Excercise", "This is a working exercise");
		working.addCode(new JavaClass("WorkingCode",
				"public class WorkingCode {\n\n\tpublic int returnOne() {\n\t\treturn 1;\n\t}\n}"));
		working.addTest(new JavaClass("WorkingTest",
				"import static org.junit.Assert.*;\nimport org.junit.Test;\n\npublic class WorkingTest {\n\n\t@Test\n\tpublic void testCode() {\n\t\tWorkingCode c = new WorkingCode();\n\t\tassertEquals(1, c.returnOne());\n\t}\n}"));
		catalog.add(working);

		Exercise compileError = new Exercise("CompileError Exercise", "This exercise has a compile error");
		compileError.addCode(new JavaClass("CompileErrorCode",
				"public class CompileErrorCode {\n\n\tpublic int returnOne() {\n\t\treturn 1;\n\t}\n}"));
		compileError.addTest(new JavaClass("CompileErrorTest",
				"import static org.junit.Assert.*;\nimport org.junit.Test;\n\npublic class CompileErrorTest {\n\n\t@Test\n\tpublic void testCode() {\n\t\tCompileErrorCode c = new CompileErrorCode();\n\t\tassertEquals(2, c.returnTwo());\n\t}\n}"));
		catalog.add(compileError);

		Exercise testError = new Exercise("TestError Excercise", "This exercise has a test error");
		testError.addCode(new JavaClass("TestErrorCode",
				"public class TestErrorCode {\n\n\tpublic int returnOne() {\n\t\treturn 2;\n\t}\n}"));
		testError.addTest(new JavaClass("TestErrorTest",
				"import static org.junit.Assert.*;\nimport org.junit.Test;\n\npublic class TestErrorTest {\n\n\t@Test\n\tpublic void testCode() {\n\t\tTestErrorCode c = new TestErrorCode();\n\t\tassertEquals(1, c.returnOne());\n\t}\n}"));
		testError.setBabyStepsCodeTime(10);
		testError.setBabyStepsTestTime(10);
		catalog.add(testError);

		Exercise notAllowedToProceedToGreenSyntaxError = new Exercise(
				"Not Allowed to proceed to Green because it has a syntax error",
				"This has a syntax error.");
		notAllowedToProceedToGreenSyntaxError.addCode(new JavaClass("WorkingCode",
				"public class WorkingCode {\n\n\tpublic int returnOne() {\n\t\treturn 1;\n\t}\n}"));
		notAllowedToProceedToGreenSyntaxError.addTest(new JavaClass("WorkingTest",
				"import static org.junit.Assert.*;\nimport org.junit.Test;\n\npublic class WorkingTest {\n\n\t@Test\n\tpublic void testCode() {\n\t\tWorkingCode c = new WorkingCode();\n\t\tassertEquals(1, c.returnOne());\n\t\n}"));
		catalog.add(notAllowedToProceedToGreenSyntaxError);

		Exercise notAllowedToProceedToGreenMissingTestAnnotation = new Exercise(
				"Not Allowed to proceed to Green because there is not method annotated with @Test",
				"No @Test");
		notAllowedToProceedToGreenMissingTestAnnotation.addCode(new JavaClass("WorkingCode",
				"public class WorkingCode {\n\n\tpublic int returnOne() {\n\t\treturn 1;\n\t}\n}"));
		notAllowedToProceedToGreenMissingTestAnnotation.addTest(new JavaClass("WorkingTest",
				"import static org.junit.Assert.*;\nimport org.junit.Test;\n\npublic class WorkingTest {\n\n\t\n\tpublic void testCode() {\n\t\tWorkingCode c = new WorkingCode();\n\t\tassertEquals(1, c.returnOne());\n\t}\n}"));
		catalog.add(notAllowedToProceedToGreenMissingTestAnnotation);

		return catalog;
	}

	@Override
	public void setXmlStream(InputStream xmlStream) {

	}

}
