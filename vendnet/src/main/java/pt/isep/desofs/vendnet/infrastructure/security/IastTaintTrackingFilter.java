package pt.isep.desofs.vendnet.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class IastTaintTrackingFilter extends OncePerRequestFilter {

    private static final Map<String, List<TaintFlow>> detectedFlows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {

        TaintAwareHttpServletResponseWrapper responseWrapper =
                new TaintAwareHttpServletResponseWrapper(response);

        String requestId = request.getHeader("X-Correlation-Id");
        if (requestId == null) {
            requestId = java.util.UUID.randomUUID().toString();
        }

        try {
            filterChain.doFilter(request, responseWrapper);
            analyzeFlows(requestId, request, responseWrapper);
        } catch (Exception e) {
            log.error("IAST filter error for request {}: {}", requestId, e.getMessage());
            throw e;
        }
    }

    private void analyzeFlows(String requestId, HttpServletRequest request,
                               TaintAwareHttpServletResponseWrapper responseWrapper) {
        List<TaintFlow> flows = new ArrayList<>();

        detectSqlInjectionTaint(request, flows);
        detectPathTraversalTaint(request, flows);
        detectCommandInjectionTaint(request, flows);

        if (!flows.isEmpty()) {
            detectedFlows.put(requestId, flows);
            log.warn("[IAST] Potential taint flows detected for request {}: {}",
                    requestId, flows.size());
            for (TaintFlow flow : flows) {
                log.warn("[IAST] {} | source={} sink={}",
                        flow.type, flow.source, flow.sink);
            }
        }
    }

    private void detectSqlInjectionTaint(HttpServletRequest request, List<TaintFlow> flows) {
        Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            for (String value : entry.getValue()) {
                if (containsSqlPattern(value)) {
                    flows.add(new TaintFlow("SQL_INJECTION",
                            "HTTP parameter '" + entry.getKey() + "'",
                            "JPA/native query execution"));
                }
            }
        }

        String queryString = request.getQueryString();
        if (queryString != null && containsSqlPattern(queryString)) {
            flows.add(new TaintFlow("SQL_INJECTION",
                    "HTTP query string", "JPA/native query execution"));
        }
    }

    private void detectPathTraversalTaint(HttpServletRequest request, List<TaintFlow> flows) {
        String path = request.getRequestURI();
        if (path != null && (path.contains("../") || path.contains("..\\"))) {
            flows.add(new TaintFlow("PATH_TRAVERSAL",
                    "HTTP request URI '" + path + "'",
                    "File system operations (NIO)"));
        }

        Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            for (String value : entry.getValue()) {
                if (value.contains("../") || value.contains("..\\")) {
                    flows.add(new TaintFlow("PATH_TRAVERSAL",
                            "HTTP parameter '" + entry.getKey() + "'",
                            "File system operations (NIO)"));
                }
            }
        }
    }

    private void detectCommandInjectionTaint(HttpServletRequest request, List<TaintFlow> flows) {
        Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            for (String value : entry.getValue()) {
                if (containsShellMetacharacters(value)) {
                    flows.add(new TaintFlow("COMMAND_INJECTION",
                            "HTTP parameter '" + entry.getKey() + "'",
                            "ProcessBuilder / OS command execution"));
                }
            }
        }

        String contentType = request.getContentType();
        if (contentType != null && request.getMethod().equals("POST")) {
            try {
                String body = request.getReader().lines()
                        .reduce("", (a, b) -> a + b);
                if (containsShellMetacharacters(body)) {
                    flows.add(new TaintFlow("COMMAND_INJECTION",
                            "HTTP request body",
                            "ProcessBuilder / OS command execution"));
                }
            } catch (Exception ignored) {
            }
        }
    }

    private boolean containsSqlPattern(String value) {
        if (value == null) return false;
        String upper = value.toUpperCase();
        return upper.contains("' OR ") || upper.contains("1=1")
                || upper.contains("UNION SELECT") || upper.contains("DROP TABLE")
                || upper.contains("--") || upper.contains(";")
                || upper.contains("OR 1=1") || upper.contains("'");
    }

    private boolean containsShellMetacharacters(String value) {
        if (value == null) return false;
        return value.contains(";") || value.contains("|") || value.contains("$")
                || value.contains("`") || value.contains("&&") || value.contains(">")
                || value.contains("<") || value.contains("(") || value.contains(")");
    }

    public static Map<String, List<TaintFlow>> getDetectedFlows() {
        return Collections.unmodifiableMap(detectedFlows);
    }

    public static void clearDetectedFlows() {
        detectedFlows.clear();
    }

    public static class TaintFlow {
        public final String type;
        public final String source;
        public final String sink;

        TaintFlow(String type, String source, String sink) {
            this.type = type;
            this.source = source;
            this.sink = sink;
        }

        @Override
        public String toString() {
            return String.format("%s: %s -> %s", type, source, sink);
        }
    }
}
