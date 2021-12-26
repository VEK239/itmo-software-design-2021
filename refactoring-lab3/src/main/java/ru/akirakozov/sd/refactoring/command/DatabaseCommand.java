package ru.akirakozov.sd.refactoring.command;

public abstract class DatabaseCommand<T> implements Command<T> {
    protected final String sqlStatement;

    public DatabaseCommand(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }
}

