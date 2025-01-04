package io.hhplus.ecommerce.global.openapi;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

@Component
public class ApiFailResponseAnnotationProcessor implements OperationCustomizer {

	@Override
	public Operation customize(final Operation operation, final HandlerMethod handlerMethod) {
		final ApiFailResponse annotation = handlerMethod.getMethodAnnotation(ApiFailResponse.class);
		if (annotation == null) {
			return operation;
		}

		final Schema<?> schema = new Schema<>()
			.addProperty("code", new Schema<>().type("integer").example(400))
			.addProperty("message", new Schema<>().type("string").example(annotation.value()))
			.addProperty("data", new Schema<>().type("object").nullable(true));

		final Content content = new Content()
			.addMediaType("application/json", new MediaType().schema(schema));

		final ApiResponse response = new ApiResponse()
			.description("실패")
			.content(content);

		operation.getResponses()
			.addApiResponse("400", response);

		return operation;
	}
}
