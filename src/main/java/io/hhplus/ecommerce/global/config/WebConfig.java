package io.hhplus.ecommerce.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.hhplus.ecommerce.global.interceptor.LoggingInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final LoggingInterceptor loggingInterceptor;

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(loggingInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/favicon.ico");
	}
}
