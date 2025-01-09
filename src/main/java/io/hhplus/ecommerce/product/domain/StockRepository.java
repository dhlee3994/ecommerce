package io.hhplus.ecommerce.product.domain;

import java.util.Optional;

public interface StockRepository {

	Optional<Stock> findByProductIdForUpdate(long productId);
}
