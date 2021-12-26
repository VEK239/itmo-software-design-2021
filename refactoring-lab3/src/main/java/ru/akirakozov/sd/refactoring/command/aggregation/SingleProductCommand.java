package ru.akirakozov.sd.refactoring.command.aggregation;

import ru.akirakozov.sd.refactoring.command.DatabaseCommand;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class SingleProductCommand extends DatabaseCommand<Product> {
    public SingleProductCommand(String sqlStatement) {
        super(sqlStatement);
    }

    @Override
    public Product execute() throws Exception {
        try (Connection c = Database.getConnection();
             Statement stmt = c.createStatement()) {
            Product p = new Product();
            try (ResultSet rs = stmt.executeQuery(sqlStatement)) {
                p.name = rs.getString("name");
                p.price = rs.getInt("price");
                p.id = rs.getInt("id");
                return p;
            }
        }
    }
}
