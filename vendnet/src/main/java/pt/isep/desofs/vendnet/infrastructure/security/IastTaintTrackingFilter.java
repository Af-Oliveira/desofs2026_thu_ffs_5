package pt.isep.desofs.vendnet.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class IastTaintTrackingFilter extends OncePerRequestFilter {

    private static final String COMMAND_INJECTION = "COMMAND_INJECTION";
    private static final String HTTP_PARAMETER_PREFIX = "HTTP parameter '";
    private static final Map<String, List<TaintFlow>> detectedFlows = new ConcurrentHashMap<>();
    private static final List<TaintFlow> confirmedExploitableFlows = new java.util.concurrent.CopyOnWriteArrayList<>();

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
        } catch (IOException | ServletException | RuntimeException e) {
            log.error("IAST filter error for request {}: {}", requestId, e.getMessage());
            throw e;
        }
    }

    private void analyzeFlows(String requestId, HttpServletRequest request,
                               TaintAwareHttpServletResponseWrapper responseWrapper) {
        List<TaintFlow> rawFlows = new ArrayList<>();

        detectSqlInjectionTaint(request, rawFlows);
        detectPathTraversalTaint(request, rawFlows);
        detectCommandInjectionTaint(request, rawFlows);

        if (rawFlows.isEmpty()) return;

        String uri = request.getRequestURI();
        int status = responseWrapper.getStatus();
        boolean twoXx = status >= 200 && status < 300;

        List<TaintFlow> enriched = new ArrayList<>(rawFlows.size());
        for (TaintFlow raw : rawFlows) {
            // COMMAND_INJECTION reaching a 2xx response = confirmed exploitable.
            // SQL injection is protected by JPA parameterized queries (not confirmed).
            // Path traversal is protected by PathValidator (not confirmed).
            boolean confirmed = COMMAND_INJECTION.equals(raw.type) && twoXx;
            enriched.add(new TaintFlow(raw.type, raw.source, raw.sink, uri, confirmed));
        }

        detectedFlows.put(requestId, enriched);
        log.warn("[IAST] {} taint flow(s) for {} {} (HTTP {})",
                enriched.size(), request.getMethod(), uri, status);

        for (TaintFlow flow : enriched) {
            if (flow.confirmedExploitable) {
                confirmedExploitableFlows.add(flow);
                log.error("[IAST][CONFIRMED-EXPLOITABLE] {} | source={} uri={}",
                        flow.type, flow.source, flow.requestUri);
            } else {
                log.warn("[IAST] {} | source={} sink={} uri={}",
                        flow.type, flow.source, flow.sink, flow.requestUri);
            }
        }
    }

    private void detectSqlInjectionTaint(HttpServletRequest request, List<TaintFlow> flows) {
        Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            for (String value : entry.getValue()) {
                if (containsSqlPattern(value)) {
                    flows.add(new TaintFlow("SQL_INJECTION",
                            HTTP_PARAMETER_PREFIX + entry.getKey() + "'",
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
                            HTTP_PARAMETER_PREFIX + entry.getKey() + "'",
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
                    flows.add(new TaintFlow(COMMAND_INJECTION,
                            HTTP_PARAMETER_PREFIX + entry.getKey() + "'",
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
                    flows.add(new TaintFlow(COMMAND_INJECTION,
                            "HTTP request body",
                            "ProcessBuilder / OS command execution"));
                }
            } catch (IOException | IllegalStateException ignored) {
                // Request body may already be consumed by the application; skip body taint checks.
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

    /** All flows confirmed exploitable across requests since last {@link #clearConfirmedExploitableFlows()}. */
    public static List<TaintFlow> getConfirmedExploitableFlows() {
        return java.util.Collections.unmodifiableList(confirmedExploitableFlows);
    }

    /** Call in {@code @BeforeAll} of IAST tests to start each run from a clean slate. */
    public static void clearConfirmedExploitableFlows() {
        confirmedExploitableFlows.clear();
    }

    /**
     * A detected taint flow from an HTTP source to a dangerous sink.
     *
     * <p>Source-line correlation: {@code requestUri} maps the finding to the controller that owns
     * the route (e.g. {@code /api/admin/operations/backup} → {@code OperationsController →
     * BackupServiceImpl.generateBackup()}). Use the project route table in the IAST README for the
     * full URI-to-controller mapping.
     */
    public static class TaintFlow {
        public final String type;
        public final String source;
        public final String sink;
        /** Request URI — maps to a specific controller method for source-line correlation. */
        public final String requestUri;
        /** True when command-injection taint reached a 2xx response. */
        public final boolean confirmedExploitable;

        /** Used internally by detect methods; {@code requestUri} and {@code confirmedExploitable} enriched in analyzeFlows. */
        TaintFlow(String type, String source, String sink) {
            this(type, source, sink, null, false);
        }

        TaintFlow(String type, String source, String sink, String requestUri, boolean confirmedExploitable) {
            this.type = type;
            this.source = source;
            this.sink = sink;
            this.requestUri = requestUri;
            this.confirmedExploitable = confirmedExploitable;
        }

        @Override
        public String toString() {
            return String.format("%s: %s -> %s [uri=%s, confirmed=%b]",
                    type, source, sink, requestUri, confirmedExploitable);
        }
    }
}
