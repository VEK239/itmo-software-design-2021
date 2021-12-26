package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.command.AddNewProductCommand;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.exceptions.BadModelException;
import ru.akirakozov.sd.refactoring.model.Product;

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
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(p.toString());
        } catch (BadModelException e) {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(e.getMessage());
        }
    }
}
