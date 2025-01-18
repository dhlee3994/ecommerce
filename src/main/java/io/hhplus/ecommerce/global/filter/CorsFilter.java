package io.hhplus.ecommerce.global.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class CorsFilter implements Filter {

	private final Set<String> allowedOrigins;

	public CorsFilter(@Value("${cors.allowed-origins}") final Set<String> allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	@Override
	public void doFilter(
		final ServletRequest servletRequest,
		final ServletResponse servletResponse,
		final FilterChain chain
	) throws IOException, ServletException {

		final HttpServletResponse response = (HttpServletResponse)servletResponse;
		final HttpServletRequest request = (HttpServletRequest)servletRequest;

		final String origin = request.getHeader(HttpHeaders.ORIGIN);
		if (isNotSameOrigin(origin) && isNotAllowedOrigin(origin)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
		response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS");
		response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization");
		response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
		response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");

		if (isPreflightRequest(request)) {
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		chain.doFilter(servletRequest, servletResponse);
	}

	private static boolean isNotSameOrigin(final String origin) {
		return origin != null;
	}

	private boolean isNotAllowedOrigin(final String origin) {
		return !allowedOrigins.contains(origin);
	}

	private static boolean isPreflightRequest(final HttpServletRequest request) {
		return "OPTIONS".equals(request.getMethod());
	}
}
