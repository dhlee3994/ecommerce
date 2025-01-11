package io.hhplus.ecommerce.product.domain.spec;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ProductSearchSpec {

	private final String name;
}
