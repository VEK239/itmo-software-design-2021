package ru.akirakozov.sd.refactoring.command;

import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.model.Product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GetCommand extends DatabaseCommand<List<Product>> {
    public GetCommand() {
        super("SELECT * FROM PRODUCT");
    }

    @Override
    public List<Product> execute() {
        List<Product> products = new ArrayList<>();
        try {
            try (Connection c = Database.getConnection();
                 Statement stmt = c.createStatement()) {

                ResultSet rs = stmt.executeQuery(sqlStatement);

                while (rs.next()) {
                    Product p = new Product();
                    p.id = rs.getInt("id");
                    p.name = rs.getString("name");
                    p.price = rs.getInt("price");
                    products.add(p);
                }
                rs.close();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return products;
    }
}
