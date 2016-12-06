package tddtrainer.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import tddtrainer.catalog.Exercise;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.JavaCodeChangeEvent;
import tddtrainer.events.JavaCodeChangeEvent.CodeType;
import vk.core.api.CompilationUnit;
import vk.core.api.CompilerFactory;
import vk.core.api.JavaStringCompiler;

public class AutoCompiler {

    private EventBus bus;

    Logger logger = LoggerFactory.getLogger(AutoCompiler.class);

    private Exercise exercise;

    private String code;
    private String test;

    @Inject
    public AutoCompiler(EventBus bus) {
        this.bus = bus;
        bus.register(this);
    }

    @Subscribe
    private void codeChanged(JavaCodeChangeEvent event) {
        logger.debug("Changed {} detected, recompiling", event.getType());
        storeChangedCode(event);
        compileAndPost();
    }

    private void storeChangedCode(JavaCodeChangeEvent event) {
        if (event.getType() == CodeType.CODE) {
            code = event.getText();
        } else {
            test = event.getText();
        }
    }

    public void compileAndPost() {
        AutoCompilerResult compilerResult = recompile();
        bus.post(compilerResult);
    }

    public synchronized AutoCompilerResult recompile() {
        CompilationUnit codeCU = new CompilationUnit(exercise.getCode().getName(), code.replaceAll("\t", "    "),
                false);
        CompilationUnit testCU = new CompilationUnit(exercise.getTest().getName(), test.replaceAll("\t", "    "), true);
        JavaStringCompiler compiler = CompilerFactory.getCompiler(codeCU, testCU);
        compiler.compileAndRunTests();
        AutoCompilerResult result = new AutoCompilerResult(compiler, exercise);
        return result;
    }

    @Subscribe
    private synchronized void exerciseChange(ExerciseEvent event) {
        exercise = event.getExercise();
        code = exercise.getCode().getCode();
        test = exercise.getTest().getCode();
        logger.debug("Exercise changed {}", event.getExercise().getName());
    }

}
