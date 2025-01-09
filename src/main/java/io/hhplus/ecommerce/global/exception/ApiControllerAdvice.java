package io.hhplus.ecommerce.global.exception;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.hhplus.ecommerce.global.CommonApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

	@ExceptionHandler(InvalidRequestException.class)
	public CommonApiResponse<Void> handleInvalidRequestException(final InvalidRequestException e) {
		log.error(e.getMessage(), e);
		return CommonApiResponse.badRequest(e.getMessage());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public CommonApiResponse<Void> handleEntityNotFoundException(final EntityNotFoundException e) {
		log.error(e.getMessage(), e);
		return CommonApiResponse.badRequest(e.getMessage());
	}
}