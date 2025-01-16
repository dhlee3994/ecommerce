package io.hhplus.ecommerce.global.filter;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@SpringBootTest
class CorsFilterIntegrationTest {

	@Value("${cors.allowed-origins}")
	private Set<String> allowedOrigins;

	@Autowired
	private MockMvc mvc;

	@DisplayName("Preflight 요청시 CORS 설정이 제대로 동작한다.")
	@MethodSource("provideAllowedOrigins")
	@ParameterizedTest
	void corsPreflight(final String allowedOrigin) throws Exception {
		mvc.perform(options("/api/v1/products")
				.header("Origin", allowedOrigin)
				.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().isOk())
			.andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin))
			.andExpect(header().string(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS"))
			.andExpect(header().string(ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization"))
			.andExpect(header().string(ACCESS_CONTROL_MAX_AGE, "3600"))
			.andDo(print());
	}

	@DisplayName("실제 요청시 CORS 설정이 제대로 동작한다.")
	@MethodSource("provideAllowedOrigins")
	@ParameterizedTest
	void corsNotPreflight(final String allowedOrigin) throws Exception {
		mvc.perform(get("/api/v1/products")
				.header("Origin", allowedOrigin))
			.andExpect(status().isOk())
			.andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin))
			.andExpect(header().string(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS"))
			.andExpect(header().string(ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization"))
			.andExpect(header().string(ACCESS_CONTROL_MAX_AGE, "3600"))
			.andDo(print());
	}

	private Stream<String> provideAllowedOrigins() {
		return allowedOrigins.stream();
	}

	@DisplayName("Origin이 설정되지 않은 요청을 들어오면 Same Origin으로 간주하고 CORS 설정이 제대로 동작한다.")
	@Test
	void corsNull() throws Exception {
		mvc.perform(options("/api/v1/products"))
			.andExpect(status().isOk())
			.andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN))
			.andExpect(header().string(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS"))
			.andExpect(header().string(ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization"))
			.andExpect(header().string(ACCESS_CONTROL_MAX_AGE, "3600"))
			.andDo(print());
	}

	@DisplayName("유효하지 않은 Origin으로 요청이 들어오면 403 응답코드를 반환한다.")
	@Test
	void corsInvalid() throws Exception {
		mvc.perform(options("/api/v1/products")
				.header("Origin", "https://invalid-cors.com")
				.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().isForbidden())
			.andDo(print());
	}
}
