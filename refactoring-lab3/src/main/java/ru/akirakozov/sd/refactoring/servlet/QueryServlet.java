package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.command.aggregation.*;
import ru.akirakozov.sd.refactoring.model.Product;

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

        if ("max".equals(command)) {
            try {
                try {
                    Product result = new GetProductWithMaxPriceCommand().execute();
                    response.getWriter().println("<html><body>");
                    response.getWriter().println("<h1>Product with max price: </h1>");
                    response.getWriter().println(result.name + "\t" + result.price + "</br>");
                    response.getWriter().println("</body></html>");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("min".equals(command)) {
            try {
                Product result = new GetProductWithMinPriceCommand().execute();
                response.getWriter().println("<html><body>");
                response.getWriter().println("<h1>Product with min price: </h1>");
                response.getWriter().println(result.name + "\t" + result.price + "</br>");
                response.getWriter().println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("sum".equals(command)) {
            try {

                int result = new GetProductsCountCommand().execute();
                response.getWriter().println("<html><body>");
                response.getWriter().println("Summary price: ");
                response.getWriter().println(result);
                response.getWriter().println("</body></html>");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ("count".equals(command)) {
            try {
                int result = new GetProductsCountCommand().execute();
                response.getWriter().println("<html><body>");
                response.getWriter().println("Number of products: ");
                response.getWriter().println(result);
                response.getWriter().println("</body></html>");

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            response.getWriter().println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
