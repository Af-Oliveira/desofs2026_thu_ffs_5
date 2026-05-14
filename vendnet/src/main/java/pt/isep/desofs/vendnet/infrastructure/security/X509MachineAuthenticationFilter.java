package pt.isep.desofs.vendnet.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class X509MachineAuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		X509Certificate[] certs =
				(X509Certificate[]) request.getAttribute("jakarta.servlet.request.X509Certificate");

		if (certs != null && certs.length > 0) {
			X509Certificate cert = certs[0];
			String dn = cert.getSubjectX500Principal().getName();
			String cn = extractCN(dn);

			if (cn == null) {
				log.warn("mTLS connection without valid CN in certificate");
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid machine certificate");
				return;
			}

			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(
							cn, null, List.of(new SimpleGrantedAuthority("ROLE_MACHINE")));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("Machine authenticated via mTLS: {}", cn);
		}

		filterChain.doFilter(request, response);
	}

	private String extractCN(String dn) {
		for (String part : dn.split(",")) {
			String trimmed = part.trim();
			if (trimmed.startsWith("CN=") || trimmed.startsWith("cn=")) {
				return trimmed.substring(3);
			}
		}
		return null;
	}
}
