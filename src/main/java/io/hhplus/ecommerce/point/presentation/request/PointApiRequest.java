package io.hhplus.ecommerce.point.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "포인트 조회 요청")
@Getter
public class PointApiRequest {

	@Schema(description = "사용자 ID", example = "1")
	private final Long userId;

	@Builder
	private PointApiRequest(final Long userId) {
		this.userId = userId;
	}
}
