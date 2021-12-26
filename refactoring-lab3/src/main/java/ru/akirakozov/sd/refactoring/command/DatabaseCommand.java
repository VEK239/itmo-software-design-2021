package ru.akirakozov.sd.refactoring.command;

abstract class DatabaseCommand<T> implements Command<T> {
    final String sqlStatement;

    DatabaseCommand(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }
}

