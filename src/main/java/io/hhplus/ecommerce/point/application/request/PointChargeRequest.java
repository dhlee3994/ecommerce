package io.hhplus.ecommerce.point.application.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PointChargeRequest {

	private final Long userId;
	private final int chargePoint;
}
