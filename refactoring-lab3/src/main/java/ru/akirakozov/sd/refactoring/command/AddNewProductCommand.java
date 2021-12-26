package ru.akirakozov.sd.refactoring.command;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.exceptions.BadModelException;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.model.ProductValidator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AddNewProductCommand extends DatabaseCommand<Product> {
    private Product toCreate;

    @Override
    public Product execute() throws BadModelException {
        ProductValidator.validate(toCreate);
        try {
            try (Connection c = Database.getConnection();
                 PreparedStatement stmt = c.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, toCreate.name);
                stmt.setInt(2, toCreate.price);
                int rows = stmt.executeUpdate();
                if(rows == 0)
                {
                    throw new Exception("Creation unsuccessful");
                }
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        toCreate.id = (int) generatedKeys.getLong(1);
                    }
                    else {
                        throw new Exception("Creating product failed, no ID obtained.");
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return toCreate;
    }

    public AddNewProductCommand(Product toCreate) {
        super("INSERT INTO PRODUCT " +
                "(NAME, PRICE) VALUES (?, ?)");
        this.toCreate = toCreate;
    }
}
