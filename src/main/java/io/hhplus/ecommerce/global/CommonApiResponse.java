package io.hhplus.ecommerce.global;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "공통 API 응답")
@Getter
public class CommonApiResponse<T> {

	@Schema(description = "HTTP 상태 코드")
	private final int code;

	@Schema(description = "예외 메시지")
	private final String message;

	@Schema(description = "응답 데이터")
	private final T data;

	private CommonApiResponse(final int code, final String message, final T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public static <T> CommonApiResponse<T> of(final int code, final String message, final T data) {
		return new CommonApiResponse<>(code, message, data);
	}

	public static <T> CommonApiResponse<T> ok(final T data) {
		return of(HttpStatus.OK.value(), null, data);
	}

	public static <T> CommonApiResponse<T> badRequest(final String message) {
		return of(HttpStatus.BAD_REQUEST.value(), message, null);
	}
	public static <T> CommonApiResponse<T> error(final String message) {
		return of(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
	}
}
