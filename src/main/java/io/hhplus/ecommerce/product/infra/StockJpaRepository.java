package io.hhplus.ecommerce.product.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.product.domain.Stock;

public interface StockJpaRepository extends JpaRepository<Stock, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from Stock s where s.productId = :productId")
	Optional<Stock> findByProductIdForUpdate(long productId);
}
