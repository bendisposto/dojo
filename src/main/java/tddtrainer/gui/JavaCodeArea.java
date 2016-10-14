package tddtrainer.gui;

import static javafx.scene.input.KeyCode.*;
import static org.fxmisc.wellbehaved.event.EventPattern.*;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 * Source: The code is mainly copied from GitHub RichTextFX Repository but
 * modified to fit to this project. The ANNOTATION_PATTERN was added.
 * (https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/
 * main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java)
 * 
 * @author TomasMikula
 *
 */
public class JavaCodeArea extends CodeArea {

    private static final String[] KEYWORDS = new String[] { "abstract", "assert", "boolean", "break", "byte", "case",
            "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends",
            "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface",
            "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static",
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
            "volatile", "while" };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final String ANNOTATION_PATTERN = "\u0040[^\n]*";

    private static final Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<PAREN>"
            + PAREN_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<ANNOTATION>"
            + ANNOTATION_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")");

    private ExecutorService executor;

    EventHandler<? super KeyEvent> tabHandler = EventHandlerHelper
            .on(keyPressed(TAB)).act(event -> this.replaceSelection("    "))
            .create();

    EventHandler<? super KeyEvent> enterHandler = EventHandlerHelper
            .on(keyPressed(ENTER)).act(event -> {
                int index = this.getCurrentParagraph();
                String text = this.getParagraph(index).toString();
                if (this.getCaretColumn() < text.length() || this.getCaretColumn() == 0) {
                    this.replaceSelection("\n");
                } else {
                    this.replaceSelection("\n" + getIndentionString(text));
                }
            })
            .create();

    private String getIndentionString(String previous) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < previous.length(); j++) {
            char c = previous.charAt(j);
            if (Character.isWhitespace(c))
                sb.append(c);
            else
                break;
        }
        return sb.toString();
    }

    public JavaCodeArea() {
        EventHandlerHelper.install(this.onKeyPressedProperty(), tabHandler);
        EventHandlerHelper.install(this.onKeyPressedProperty(), enterHandler);
        executor = Executors.newSingleThreadExecutor();
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        this.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(250)).supplyTask(this::computeHighlightingAsync)
                .awaitLatest(this.richChanges()).filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                }).subscribe(this::applyHighlighting);

        this.getStylesheets().add(this.getClass().getResource("java-keywords.css").toExternalForm());
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = this.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);

        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        this.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword"
                    : matcher.group("PAREN") != null ? "paren"
                            : matcher.group("BRACE") != null ? "brace"
                                    : matcher.group("BRACKET") != null ? "bracket"
                                            : matcher.group("SEMICOLON") != null ? "semicolon"
                                                    : matcher.group("STRING") != null ? "string"
                                                            : matcher.group("ANNOTATION") != null ? "annotation"
                                                                    : matcher.group("COMMENT") != null ? "comment"
                                                                            : null;
            /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public void disable(boolean disable) {
        this.setEditable(!disable);
        if (disable) {
            this.getStylesheets().remove(this.getClass().getResource("java-keywords.css").toExternalForm());
            this.getStylesheets().add(this.getClass().getResource("java-keywords-disabled.css").toExternalForm());
        } else {
            this.getStylesheets().remove(this.getClass().getResource("java-keywords-disabled.css").toExternalForm());
            this.getStylesheets().add(this.getClass().getResource("java-keywords.css").toExternalForm());
        }
    }

    @Override
    public void appendText(String text) {
        text = text.replace("\t", "    ");
        super.appendText(text);
    }

}
