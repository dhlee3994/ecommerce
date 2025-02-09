package io.hhplus.ecommerce.product.domain;

import java.util.List;

import io.hhplus.ecommerce.product.application.response.BestProductResponse;

public interface ProductCacheRepository {

	List<BestProductResponse> getBestProducts();

	void saveBestProducts(List<BestProductResponse> bestProducts);
}
