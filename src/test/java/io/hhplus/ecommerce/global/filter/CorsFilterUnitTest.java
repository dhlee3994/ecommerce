package io.hhplus.ecommerce.global.filter;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_MAX_AGE;

import jakarta.servlet.FilterChain;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CorsFilterUnitTest {

	private static final String ALLOWED_ORIGIN = "http://localhost:3000";

	private CorsFilter corsFilter;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private FilterChain filterChain;

	@BeforeEach
	void setUp() {
		corsFilter = new CorsFilter(Set.of(ALLOWED_ORIGIN));

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = mock(FilterChain.class);
	}

	@DisplayName("Preflight 요청시 CORS 설정이 제대로 동작하고, 다음 필터로 진행하지 않는다.")
	@Test
	void corsPreflight() throws Exception {
		// given
		request.setMethod("OPTIONS");
		request.addHeader("Origin", ALLOWED_ORIGIN);

		// when
		corsFilter.doFilter(request, response, filterChain);

		// then
		assertThat(response.getStatus()).isEqualTo(SC_OK);
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo(ALLOWED_ORIGIN);
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_METHODS)).isEqualTo("GET, POST, PUT, DELETE, OPTIONS");
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS)).isEqualTo("Content-Type, Authorization");
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS)).isEqualTo("true");
		assertThat(response.getHeader(ACCESS_CONTROL_MAX_AGE)).isEqualTo("3600");

		verify(filterChain, never()).doFilter(request, response);
	}

	@DisplayName("실제 요청시 CORS 설정이 제대로 동작하고, 다음 필터로 진행한다.")
	@Test
	void corsNotPreflight() throws Exception {
		// given
		request.setMethod("GET");
		request.addHeader("Origin", ALLOWED_ORIGIN);

		// when
		corsFilter.doFilter(request, response, filterChain);

		// then
		assertThat(response.getStatus()).isEqualTo(SC_OK);
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo(ALLOWED_ORIGIN);
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_METHODS)).isEqualTo("GET, POST, PUT, DELETE, OPTIONS");
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS)).isEqualTo("Content-Type, Authorization");
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS)).isEqualTo("true");
		assertThat(response.getHeader(ACCESS_CONTROL_MAX_AGE)).isEqualTo("3600");

		verify(filterChain, times(1)).doFilter(request, response);
	}

	@DisplayName("Origin이 설정되지 않은 요청을 들어오면 Same Origin으로 간주하고 CORS 설정이 제대로 동작한다.")
	@Test
	void corsNull() throws Exception {
		// given
		request.setMethod("GET");

		// when
		corsFilter.doFilter(request, response, filterChain);

		// then
		assertThat(response.getStatus()).isEqualTo(SC_OK);
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN)).isNull();
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_METHODS)).isEqualTo("GET, POST, PUT, DELETE, OPTIONS");
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS)).isEqualTo("Content-Type, Authorization");
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS)).isEqualTo("true");
		assertThat(response.getHeader(ACCESS_CONTROL_MAX_AGE)).isEqualTo("3600");

		verify(filterChain, times(1)).doFilter(request, response);
	}

	@DisplayName("유효하지 않은 Origin으로 요청이 들어오면 403 응답코드를 반환한다.")
	@Test
	void corsInvalid() throws Exception {
		// given
		request.setMethod("OPTIONS");
		request.addHeader("Origin", ALLOWED_ORIGIN + "invalid");

		// when
		corsFilter.doFilter(request, response, filterChain);

		// then
		assertThat(response.getStatus()).isEqualTo(SC_FORBIDDEN);
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN)).isNull();
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_METHODS)).isNull();
		assertThat(response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS)).isNull();
		assertThat(response.getHeader(ACCESS_CONTROL_MAX_AGE)).isNull();

		verify(filterChain, never()).doFilter(request, response);
	}


}
