package io.hhplus.ecommerce.global.interceptor;

import static io.hhplus.ecommerce.global.filter.MdcKey.REQUEST_ID;
import static io.hhplus.ecommerce.global.filter.MdcKey.REQUEST_IP;
import static io.hhplus.ecommerce.global.filter.MdcKey.REQUEST_METHOD;
import static io.hhplus.ecommerce.global.filter.MdcKey.REQUEST_URI;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

	private static final String START_TIME = "startTime";

	@Override
	public boolean preHandle(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final Object handler
	) throws Exception {
		final String requestId = MDC.get(REQUEST_ID.name());
		final String requestMethod = MDC.get(REQUEST_METHOD.name());
		final String requestUri = MDC.get(REQUEST_URI.name());
		final String requestIp = MDC.get(REQUEST_IP.name());
		log.info("[START] [{} - {}] {} {}", requestId, requestIp, requestMethod, requestUri);

		request.setAttribute(START_TIME, System.currentTimeMillis());
		return true;
	}

	@Override
	public void afterCompletion(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final Object handler,
		final Exception ex
	) throws Exception {

		final long startTime = (long)request.getAttribute(START_TIME);
		final long durationMs = System.currentTimeMillis() - startTime;

		final String requestId = MDC.get(REQUEST_ID.name());
		final String requestMethod = MDC.get(REQUEST_METHOD.name());
		final String requestUri = MDC.get(REQUEST_URI.name());
		final String requestIp = MDC.get(REQUEST_IP.name());
		log.info("[END] [{} - {}] {} {} took {}ms", requestId, requestIp, requestMethod, requestUri, durationMs);

		if (ex != null) {
			log.error("Exception: ", ex);
		}
	}
}
