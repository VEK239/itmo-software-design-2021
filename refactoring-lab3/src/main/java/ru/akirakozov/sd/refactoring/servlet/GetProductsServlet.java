package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.command.GetCommand;
import ru.akirakozov.sd.refactoring.database.Database;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.servlet.html.HtmlBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        HtmlBuilder hb = new HtmlBuilder();
        try {
            List<Product> products = new GetCommand().execute();
            for (Product p : products) {
                hb.addLine(p.name + "\t" + p.price);
            }
            response.getWriter().println(hb.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
