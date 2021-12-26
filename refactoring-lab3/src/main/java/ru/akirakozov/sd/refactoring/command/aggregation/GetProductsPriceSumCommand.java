package ru.akirakozov.sd.refactoring.command.aggregation;

import ru.akirakozov.sd.refactoring.command.DatabaseCommand;
import ru.akirakozov.sd.refactoring.database.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class GetProductsPriceSumCommand extends DatabaseCommand<Integer> {
    public GetProductsPriceSumCommand() {
        super("SELECT SUM(price) FROM PRODUCT");
    }

    @Override
    public Integer execute() throws Exception {
        try (Connection c = Database.getConnection();
             Statement stmt = c.createStatement()) {

            ResultSet rs = stmt.executeQuery(sqlStatement);
            return rs.getInt(1);
        }
    }
}
