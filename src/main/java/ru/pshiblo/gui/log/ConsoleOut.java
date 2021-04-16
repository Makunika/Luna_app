package ru.pshiblo.gui.log;

import javafx.scene.control.TextArea;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

public class ConsoleOut {

    private static final TextArea textArea = new TextArea();

    private static final PrintStream out = new PrintStream(new TextAreaOutputStream(textArea));

    public static void println(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        out.println("[" + dateFormat.format(new Date()) + "] - " + msg);
        System.out.println("[" + dateFormat.format(new Date()) + "] - " + msg);
    }

    public static <T> void printList(Collection<T> collection, String msg) {
        StringBuilder sb = new StringBuilder(msg + " (size = " + collection.size() + "): ");
        collection.forEach(t -> sb.append(t.toString()).append(" | "));
        println(sb.toString());
    }

    public static <T> void printList(Collection<T> collection, String msg, Function<T, String> handle) {
        StringBuilder sb = new StringBuilder(msg + " (size = " + collection.size() + "): ");
        collection.forEach(t -> sb.append(handle.apply(t)).append(" | "));
        println(sb.toString());
    }

    public static TextArea getTextArea() {
        return textArea;
    }

    public static void alert(String message) {
        println("[ALERT] " + message);
    }
}
