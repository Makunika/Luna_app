package ru.pshiblo.services.youtube.listener;

import javafx.beans.property.SimpleStringProperty;

public class UpdatedCommand {

    private static final UpdatedCommand updatedCommand = new UpdatedCommand();

    public static UpdatedCommand getInstance() {
        return updatedCommand;
    }

    private final SimpleStringProperty command;
    private final SimpleStringProperty answer;


    public UpdatedCommand() {
        answer = new SimpleStringProperty("loh ti");
        command = new SimpleStringProperty("/loh");
    }

    public String getCommand() {
        return command.get();
    }

    public SimpleStringProperty commandProperty() {
        return command;
    }

    public String getAnswer() {
        return answer.get();
    }

    public SimpleStringProperty answerProperty() {
        return answer;
    }
}
