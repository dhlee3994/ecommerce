package io.hhplus.ecommerce.product.infra;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.product.domain.Stock;

@Profile("!optimistic-lock & !pessimistic-lock")
public interface StockJpaRepository extends JpaRepository<Stock, Long> {

	@Query("select s from Stock s where s.productId = :productId")
	Optional<Stock> findByProductIdForUpdate(long productId);
}
