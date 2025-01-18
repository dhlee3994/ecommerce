package io.hhplus.ecommerce.global.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Component
public class MdcFilter implements Filter {

	private static final String FAVICON_REQUEST = "/favicon.ico";

	@Override
	public void doFilter(
		final ServletRequest request,
		final ServletResponse response,
		final FilterChain chain
	) throws IOException, ServletException {

		final HttpServletRequest httpServletRequest = (HttpServletRequest)request;
		if (FAVICON_REQUEST.equals(httpServletRequest.getRequestURI())) {
			return;
		}

		try {
			setMdc(httpServletRequest);
			chain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	private void setMdc(final HttpServletRequest request) {
		MDC.put(MdcKey.REQUEST_ID.name(), UUID.randomUUID().toString().substring(0, 8));
		MDC.put(MdcKey.REQUEST_IP.name(), request.getRemoteAddr());
		MDC.put(MdcKey.REQUEST_METHOD.name(), request.getMethod());
		MDC.put(MdcKey.REQUEST_URI.name(), request.getRequestURI());
	}
}
