package tddtrainer.gui;

import static tddtrainer.events.JavaCodeChangeEvent.CodeType.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import tddtrainer.catalog.Exercise;
import tddtrainer.compiler.AutoCompilerResult;
import tddtrainer.events.ExerciseEvent;
import tddtrainer.events.JavaCodeChangeEvent;
import tddtrainer.events.automaton.ResetPhaseEvent;
import tddtrainer.events.automaton.SwitchedToGreenEvent;
import tddtrainer.events.automaton.SwitchedToRedEvent;
import tddtrainer.events.automaton.SwitchedToRefactorEvent;

public class EditorViewController extends SplitPane implements Initializable {

    private WebView tests;
    private WebView code;

    @FXML
    private TextArea console;
    @FXML
    private TextArea testoutput;

    @FXML
    private AnchorPane codePane;

    @FXML
    private AnchorPane testPane;

    @FXML
    private Label codeLabel;

    @FXML
    private Label testLabel;

    @FXML
    private HBox iGreenBox;

    @FXML
    private Label iRedLabel1;

    @FXML
    private HBox codeBox;

    @FXML
    HBox statuscontainer;

    @FXML
    Label status;

    Logger logger = LoggerFactory.getLogger(EditorViewController.class);

    String revertToCode;
    String revertToTest;
    private final EventBus bus;
    private int fontSize = 15;

    @Inject
    public EditorViewController(FXMLLoader loader, EventBus bus) {
        this.bus = bus;
        bus.register(this);
        URL resource = getClass().getResource("EditorView.fxml");
        loader.setLocation(resource);
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            logger.error("Error loading Root view", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        iGreenBox.setVisible(false);
        addEditors();
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 5.0);
        AnchorPane.setRightAnchor(this, 5.0);
        AnchorPane.setTopAnchor(this, 60.0);

        console.setStyle("-fx-font-family:monospace;");

    }

    @Subscribe
    public void showExercise(ExerciseEvent exerciseEvent) {
        Exercise exercise = exerciseEvent.getExercise();
        if (exercise != null) {
            showExercise(exercise);
        }
    }

    public String getCode() {
        return (String) code.getEngine().executeScript("editor.getValue()");
    }

    public String getTest() {
        return (String) tests.getEngine().executeScript("editor.getValue()");
    }

    public void compile(String type, String text) {
        JavaCodeChangeEvent.CodeType t = "code".equals(type) ? CODE : TEST;
        JavaCodeChangeEvent event = new JavaCodeChangeEvent(text, t);
        bus.post(event);
    }

    public void showExercise(Exercise exercise) {
        String jscallCode = "editor.setValue('" + exercise.getCode().getCode().replaceAll("\\n", "\\\\n") + "')";
        code.getEngine().executeScript(jscallCode);
        codeLabel.setText(exercise.getCode().getName());
        String jscallTest = "editor.setValue('" + exercise.getTest().getCode().replaceAll("\\n", "\\\\n") + "')";
        tests.getEngine().executeScript(jscallTest);
        testLabel.setText(exercise.getTest().getName());
        clearHistory();
        // code.clear();
        // code.appendText();
        // tests.clear();
        // tests.appendText(exercise.getTest().getCode());
        // revertToCode = code.getText();
        // revertToTest = tests.getText();
        // code.selectRange(0, 0);
        // tests.selectRange(0, 0);

    }

    private void clearHistory() {
        code.getEngine().executeScript("clearHistory()");
        tests.getEngine().executeScript("clearHistory()");
    }

    @Subscribe
    private void resetToRed(ResetPhaseEvent event) {
        String jscallCode = "editor.setValue('" + revertToCode.replaceAll("\\n", "\\\\n") + "')";
        code.getEngine().executeScript(jscallCode);
        String jscallTest = "editor.setValue('" + revertToTest.replaceAll("\\n", "\\\\n") + "')";
        tests.getEngine().executeScript(jscallTest);
        clearHistory();
    }

    @Subscribe
    private void changePhaseToRed(SwitchedToRedEvent event) {
        code.getEngine().executeScript("editor.setOption('readOnly', true)");
        tests.getEngine().executeScript("editor.setOption('readOnly', false)");
        revertToTest = getTest();
        revertToCode = getCode();
        clearHistory();
        tests.getEngine().executeScript("activate('crimson')");
        code.getEngine().executeScript("deactivate()");
        iGreenBox.setVisible(false);
        iRedLabel1.setText("Write code to make all tests pass");
        AnchorPane.setRightAnchor(codeBox, 15.0);
    }

    @Subscribe
    private void changePhaseToGreen(SwitchedToGreenEvent event) {
        code.getEngine().executeScript("editor.setOption('readOnly', false)");
        tests.getEngine().executeScript("editor.setOption('readOnly', true)");
        revertToTest = getTest();
        clearHistory();
        code.getEngine().executeScript("activate('forestgreen')");
        tests.getEngine().executeScript("deactivate()");
        iGreenBox.setVisible(true);
        AnchorPane.setRightAnchor(codeBox, iGreenBox.getWidth() + 10);
    }

    @Subscribe
    private void changePhaseToRefactor(SwitchedToRefactorEvent event) {
        code.getEngine().executeScript("editor.setOption('readOnly', false)");
        tests.getEngine().executeScript("editor.setOption('readOnly', false)");
        tests.getEngine().executeScript("activate('orange')");
        code.getEngine().executeScript("activate('orange')");
        clearHistory();
        iGreenBox.setVisible(true);
        iRedLabel1.setText("Modify code, but keep all tests passing");
        AnchorPane.setRightAnchor(codeBox, 15.0);
    }

    private void addEditors() {
        code = new WebView();
        code.setContextMenuEnabled(false);
        String resource = EditorViewController.class.getResource("/editor.html").toExternalForm();
        code.getEngine().load(resource);

        // code.setEditable(false);
        codePane.getChildren().add(code);
        AnchorPane.setTopAnchor(code, 50.0);
        AnchorPane.setLeftAnchor(code, 20.0);
        AnchorPane.setRightAnchor(code, 20.0);
        AnchorPane.setBottomAnchor(code, 5.0);

        tests = new WebView();
        tests.getEngine().load(resource);
        tests.setContextMenuEnabled(false);
        // tests.setEditable(false);
        testPane.getChildren().add(tests);
        AnchorPane.setTopAnchor(tests, 50.0);
        AnchorPane.setLeftAnchor(tests, 20.0);
        AnchorPane.setRightAnchor(tests, 20.0);
        AnchorPane.setBottomAnchor(tests, 5.0);

        JSObject jsobj = (JSObject) code.getEngine().executeScript("window");
        jsobj.setMember("java", this);
        jsobj.setMember("type", "code");

        jsobj = (JSObject) tests.getEngine().executeScript("window");
        jsobj.setMember("java", this);
        jsobj.setMember("type", "test");

    }

    public void zoomIn() {
        fontSize += 1;
        applyFontSize();
    }

    public void zoomOut() {
        if (fontSize > 8)
            fontSize -= 1;
        applyFontSize();
    }

    public void zoomDefault() {
        fontSize = 13;
        applyFontSize();
    }

    private void applyFontSize() {
        String style = "-fx-font-size:" + fontSize + "px";
        console.setStyle(style);
        testoutput.setStyle(style);
        code.getEngine().executeScript("changeFontSize(" + fontSize + ")");
        tests.getEngine().executeScript("changeFontSize(" + fontSize + ")");
    }

    @Subscribe
    public void compileResult(AutoCompilerResult result) {
        console.setText("Compiler Output:\n================\n" + result.getCompilerOutput());
        testoutput.setText("System.out/System.err:\n=================\n" + result.getTestOutput());
        if (result.allClassesCompile()) {
            if (result.allTestsGreen()) {
                status.setText("Code and Test compile, and the tests are passing.");
                status.setStyle("-fx-text-fill: white");
                statuscontainer.setStyle("-fx-background-color: green");
            } else {
                status.setText("Code and Test compile, but the tests are not passing.");
                status.setStyle("-fx-text-fill: white");
                statuscontainer.setStyle("-fx-background-color: red");
            }
        } else {
            status.setText("Code or Test (or both) contain errors.");
            status.setStyle("-fx-text-fill: white");
            statuscontainer.setStyle("-fx-background-color: black");
        }
    }

}
