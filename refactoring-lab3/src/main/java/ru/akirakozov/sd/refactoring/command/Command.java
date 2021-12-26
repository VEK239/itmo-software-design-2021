package ru.akirakozov.sd.refactoring.command;

public interface Command<T> {
    T execute() throws Exception;
}
