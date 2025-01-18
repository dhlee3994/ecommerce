package io.hhplus.ecommerce.global.exception;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.hhplus.ecommerce.global.CommonApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

	@ExceptionHandler(EcommerceException.class)
	public CommonApiResponse<Void> handleEcommerceException(final EcommerceException e) {
		log.error(e.getMessage(), e);
		return CommonApiResponse.badRequest(e.getMessage());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public CommonApiResponse<Void> handleEntityNotFoundException(final EntityNotFoundException e) {
		log.error(e.getMessage(), e);
		return CommonApiResponse.badRequest(e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public CommonApiResponse<Void> handleException(final Exception e) {
		log.error(e.getMessage(), e);
		return CommonApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
	}
}
