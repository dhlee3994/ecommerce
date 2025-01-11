package io.hhplus.ecommerce.global.exception;

import lombok.Getter;

@Getter
public class InvalidRequestException extends EcommerceException {

	private final ErrorCode errorCode;

	public InvalidRequestException(final ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
