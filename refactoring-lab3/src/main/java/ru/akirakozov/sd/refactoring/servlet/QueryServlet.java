package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.command.aggregation.*;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.servlet.html.HtmlBuilder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");
        HtmlBuilder hb = new HtmlBuilder();

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            switch (command) {
                case "max":
                    Product max = new GetProductWithMaxPriceCommand().execute();
                    hb.addHeader("Product with max price:")
                            .addLine(max.name + "\t" + max.price);
                    break;
                case "min":
                    Product min = new GetProductWithMinPriceCommand().execute();
                    hb.addHeader("Product with min price:")
                            .addLine(min.name + "\t" + min.price);
                    break;
                case "sum":
                    int sum = new GetProductsPriceSumCommand().execute();
                    hb.addHeader("Summary price:")
                            .addLine(sum + "");
                    break;
                case "count":
                    int result = new GetProductsCountCommand().execute();
                    hb.addHeader("Number of products:")
                            .addLine(result + "");
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    hb.addLine("Unknown command: " + command);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        response.getWriter().println(hb.toString());
    }
}
