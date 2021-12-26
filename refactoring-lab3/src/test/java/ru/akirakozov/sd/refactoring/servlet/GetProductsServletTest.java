package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

public class GetProductsServletTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private GetProductsServlet servlet;

    @BeforeEach
    public void init() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        servlet = new GetProductsServlet();

        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @AfterEach
    public void afterTest() throws SQLException {
        DatabaseUtils.clear();
    }

    @ParameterizedTest
    @MethodSource("productsTestsProvider")
    public void getProducts(String[] names, int[] prices) throws IOException, SQLException {
        for (int i = 0; i < names.length; i++) {
            DatabaseUtils.add(names[i], prices[i]);
        }

        servlet.doGet(request, response);
        String result = stringWriter.getBuffer().toString();

        assertEquals(wrapResult(names, prices), result);
    }

    public static Stream<Arguments> productsTestsProvider() {
        return Stream.of(
                arguments(new String[]{"name1"}, new int[]{1}),
                arguments(new String[]{"name1", "name2"}, new int[]{1, 2}),
                arguments(new String[]{"name1, name2", "name3"}, new int[]{1, 1, 1}),
                arguments(new String[]{}, new int[]{})
        );
    }

    static String wrapResult(String[] names, int[] prices) {
        HtmlBuilder hb = new HtmlBuilder();
        for (int i = 0; i < names.length; i++) {
            hb.addLine(names[i] + "\t" + prices[i]);
        }
        return hb.toString() + System.lineSeparator();
    }
}