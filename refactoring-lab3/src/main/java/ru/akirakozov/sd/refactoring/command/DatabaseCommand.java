package ru.akirakozov.sd.refactoring.command;

abstract class DatabaseCommand<T> implements Command<T> {
    String sqlStatement;
}

