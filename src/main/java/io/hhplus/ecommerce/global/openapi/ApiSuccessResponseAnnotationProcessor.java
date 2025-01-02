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
public class ApiSuccessResponseAnnotationProcessor implements OperationCustomizer {

	@Override
	public Operation customize(final Operation operation, final HandlerMethod handlerMethod) {
		final ApiSuccessResponse annotation = handlerMethod.getMethodAnnotation(ApiSuccessResponse.class);
		if (annotation == null) {
			return operation;
		}

		final Schema<?> schema = new Schema<>()
			.addProperty("code", new Schema<>().type("integer").example(200))
			.addProperty("message", new Schema<>().type("string").example(null))
			.addProperty("data", createDataSchema(annotation));

		final Content content = new Content()
			.addMediaType("application/json", new MediaType().schema(schema));

		final ApiResponse response = new ApiResponse()
			.description("성공")
			.content(content);

		operation.getResponses()
			.addApiResponse("200", response);

		return operation;
	}

	@SuppressWarnings("unchecked")
	private Schema<?> createDataSchema(final ApiSuccessResponse annotation) {
		final String className = annotation.value().getSimpleName();
		if (annotation.isList()) {
			return new Schema<>()
				.type("array")
				.items(new Schema<>().$ref(className));
		}
		return new Schema<>().$ref(className);
	}
}
