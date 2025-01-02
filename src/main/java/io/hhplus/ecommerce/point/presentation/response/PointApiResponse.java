package io.hhplus.ecommerce.point.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "포인트 조회 응답")
@Getter
public class PointApiResponse {

	@Schema(description = "보유 포인트 잔액", example = "10000")
	private final int point;

	@Builder
	private PointApiResponse(final int point) {
		this.point = point;
	}
}
