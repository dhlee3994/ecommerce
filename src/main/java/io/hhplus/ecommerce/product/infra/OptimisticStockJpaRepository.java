package io.hhplus.ecommerce.product.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.product.domain.Stock;

@Profile("optimistic-lock")
public interface OptimisticStockJpaRepository extends StockJpaRepository {

	@Lock(LockModeType.OPTIMISTIC)
	@Query("select s from Stock s where s.productId = :productId")
	Optional<Stock> findByProductIdForUpdate(long productId);
}
