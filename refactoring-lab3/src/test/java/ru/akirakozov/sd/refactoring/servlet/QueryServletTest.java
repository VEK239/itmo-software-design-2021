package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.akirakozov.sd.refactoring.servlet.html.HtmlBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryServletTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private QueryServlet servlet;

    @BeforeEach
    public void init() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        servlet = new QueryServlet();

        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @AfterEach
    public void afterTest() throws SQLException {
        DatabaseUtils.clear();
    }

    @ParameterizedTest
    @MethodSource("productsMaxQueryPositiveTestsProvider")
    public void positiveMaxQueryProductsTests(String[] names, int[] prices, String expected) throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("max");

        for (int i = 0; i < names.length; i++) {
            DatabaseUtils.add(names[i], prices[i]);
        }

        servlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString();

        assertEquals(wrapMaxResult(expected), result);
    }

    @ParameterizedTest
    @MethodSource("productsMinQueryPositiveTestsProvider")
    public void positiveMinQueryProductsTests(String[] names, int[] prices, String expected) throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("min");

        for (int i = 0; i < names.length; i++) {
            DatabaseUtils.add(names[i], prices[i]);
        }

        servlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString();

        assertEquals(wrapMinResult(expected), result);
    }

    @ParameterizedTest
    @MethodSource("productsCountQueryPositiveTestsProvider")
    public void positiveCountQueryProductsTests(String[] names, int[] prices, String expected) throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("count");

        for (int i = 0; i < names.length; i++) {
            DatabaseUtils.add(names[i], prices[i]);
        }

        servlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString();

        assertEquals(wrapCountResult(expected), result);
    }

    @ParameterizedTest
    @MethodSource("productsSumQueryPositiveTestsProvider")
    public void positiveSumQueryProductsTests(String[] names, int[] prices, String expected) throws IOException, SQLException {
        when(request.getParameter("command")).thenReturn("sum");

        for (int i = 0; i < names.length; i++) {
            DatabaseUtils.add(names[i], prices[i]);
        }

        servlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString();

        assertEquals(wrapSumResult(expected), result);
    }

    public static Stream<Arguments> productsMaxQueryPositiveTestsProvider() {
        return Stream.of(
                arguments(new String[]{"name1"}, new int[]{1}, "name1\t1"),
                arguments(new String[]{"name1", "name2"}, new int[]{1, 2}, "name2\t2"),
                arguments(new String[]{"name1, name2", "name3"}, new int[]{1, 1, 3}, "name3\t3"),
                arguments(new String[]{}, new int[]{}, "")
        );
    }

    public static Stream<Arguments> productsMinQueryPositiveTestsProvider() {
        return Stream.of(
                arguments(new String[]{"name1"}, new int[]{1}, "name1\t1"),
                arguments(new String[]{"name1", "name2"}, new int[]{1, 2}, "name1\t1"),
                arguments(new String[]{"name1, name2", "name3"}, new int[]{1, 2, 2}, "name1\t1"),
                arguments(new String[]{}, new int[]{}, "")
        );
    }

    public static Stream<Arguments> productsCountQueryPositiveTestsProvider() {
        return Stream.of(
                arguments(new String[]{"name1"}, new int[]{1}, "1"),
                arguments(new String[]{"name1", "name2"}, new int[]{1, 2}, "2"),
                arguments(new String[]{"name1, name2", "name3"}, new int[]{1, 1, 3}, "3"),
                arguments(new String[]{}, new int[]{}, "0")
        );
    }

    public static Stream<Arguments> productsSumQueryPositiveTestsProvider() {
        return Stream.of(
                arguments(new String[]{"name1"}, new int[]{1}, "1"),
                arguments(new String[]{"name1", "name2"}, new int[]{1, 2}, "3"),
                arguments(new String[]{"name1, name2", "name3"}, new int[]{1, 1, 3}, "5"),
                arguments(new String[]{}, new int[]{}, "0")
        );
    }

    static String wrapMaxResult(String content) {
        HtmlBuilder hb = new HtmlBuilder();
        hb.addHeader("Product with max price:").addLine(content);
        return hb.toString() + System.lineSeparator();
    }

    static String wrapMinResult(String content) {
        HtmlBuilder hb = new HtmlBuilder();
        hb.addHeader("Product with min price:").addLine(content);
        return hb.toString() + System.lineSeparator();
    }

    static String wrapCountResult(String content) {
        HtmlBuilder hb = new HtmlBuilder();
        hb.addHeader("Number of products:").addLine(content);
        return hb.toString() + System.lineSeparator();
    }

    static String wrapSumResult(String content) {
        HtmlBuilder hb = new HtmlBuilder();
        hb.addHeader("Summary price:").addLine(content);
        return hb.toString() + System.lineSeparator();
    }
}