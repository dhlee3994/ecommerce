package io.hhplus.ecommerce.global.exception;

public class EcommerceException extends RuntimeException {

	public EcommerceException(final String message) {
		super(message);
	}

	public EcommerceException(final ErrorCode errorCode) {
		super(errorCode.getMessage());
	}
}
