package org.mot.mottainaijdbc;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HelloServletTest {
    @Test
    void returnsHtmlGreeting() throws Exception {
        StringWriter output = new StringWriter();
        AtomicReference<String> contentType = new AtomicReference<>();
        HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[] {HttpServletResponse.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getWriter")) {
                        return new PrintWriter(output);
                    }
                    if (method.getName().equals("setContentType")) {
                        contentType.set((String) args[0]);
                        return null;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        HelloServlet servlet = new HelloServlet();
        servlet.init();
        servlet.doGet(null, response);
        assertEquals("text/html", contentType.get());
        assertTrue(output.toString().contains("<h1>Hello World!</h1>"));
        assertTrue(output.toString().contains("</body></html>"));
    }
}
