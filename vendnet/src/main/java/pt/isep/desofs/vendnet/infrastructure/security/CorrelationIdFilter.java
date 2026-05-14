package pt.isep.desofs.vendnet.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String correlationId = request.getHeader("X-Correlation-Id");
		if (correlationId == null || correlationId.isBlank()) {
			correlationId = UUID.randomUUID().toString();
		}

		MDC.put("correlationId", correlationId);
		MDC.put("method", request.getMethod());
		MDC.put("uri", request.getRequestURI());

		response.setHeader("X-Correlation-Id", correlationId);

		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}
}
