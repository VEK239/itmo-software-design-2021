package ru.akirakozov.sd.refactoring.servlet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AddProductServletTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private AddProductServlet servlet;

    @BeforeEach
    public void init() throws IOException {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        servlet = new AddProductServlet();

        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @AfterEach
    public void afterTest() throws SQLException {
        DatabaseUtils.clear();
    }

    @ParameterizedTest
    @MethodSource("positiveAddTestsProvider")
    public void positiveAddTest(String name, String price) throws Exception {
        when(request.getParameter("name")).thenReturn(name);
        when(request.getParameter("price")).thenReturn(price);

        servlet.doGet(request, response);

        String result = stringWriter.getBuffer().toString();
        assertEquals("OK" + System.lineSeparator(), result);
    }

    @ParameterizedTest
    @MethodSource("negativeAddTestsProvider")
    public void negativeAddTest(String name, String price) {
        assertThrows(RuntimeException.class, () -> {
            when(request.getParameter("name")).thenReturn(name);
            when(request.getParameter("price")).thenReturn(price);

            servlet.doGet(request, response);
        });
    }

    public static Stream<Arguments> positiveAddTestsProvider() {
        return Stream.of(
                arguments("test", "10")
        );
    }

    public static Stream<Arguments> negativeAddTestsProvider() {
        return Stream.of(
                arguments("", "20"),
                arguments(null, "15"),
                arguments("test", ""),
                arguments("test", null),
                arguments("test", "-10"),
                arguments("test", "0")
        );
    }
}