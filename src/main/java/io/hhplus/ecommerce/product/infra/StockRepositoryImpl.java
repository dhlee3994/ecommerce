package io.hhplus.ecommerce.product.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.product.domain.Stock;
import io.hhplus.ecommerce.product.domain.StockRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class StockRepositoryImpl implements StockRepository {

	private final StockJpaRepository stockJpaRepository;

	@Override
	public Optional<Stock> findByProductIdForUpdate(final long productId) {
		return stockJpaRepository.findByProductIdForUpdate(productId);
	}
}
