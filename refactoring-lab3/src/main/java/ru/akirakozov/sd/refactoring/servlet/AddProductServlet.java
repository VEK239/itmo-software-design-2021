package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.command.AddNewProductCommand;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.exceptions.BadModelException;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.servlet.html.HtmlBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * @author akirakozov
 */
public class AddProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Product p =new Product();
        p.name = request.getParameter("name");
        p.price = Integer.parseInt(request.getParameter("price"));

        try {
            p = new AddNewProductCommand(p).execute();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("OK");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
