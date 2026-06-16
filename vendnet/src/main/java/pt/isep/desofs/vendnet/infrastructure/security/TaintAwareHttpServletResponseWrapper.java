package pt.isep.desofs.vendnet.infrastructure.security;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class TaintAwareHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream capture = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public TaintAwareHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (writer != null) {
            throw new IllegalStateException("getWriter() already called");
        }
        if (outputStream == null) {
            outputStream = new ServletOutputStream() {
                @Override
                public boolean isReady() { return true; }

                @Override
                public void setWriteListener(WriteListener listener) {
                    // Synchronous wrapper only; async write callbacks are delegated to the container response.
                }

                @Override
                public void write(int b) throws IOException {
                    capture.write(b);
                    getResponse().getOutputStream().write(b);
                }
            };
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() already called");
        }
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(
                    getResponse().getOutputStream(), getResponse().getCharacterEncoding()));
        }
        return writer;
    }
}
