package io.hhplus.ecommerce.order.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.order.domain.Order;

@Profile("optimistic-lock")
public interface OptimisticOrderJpaRepository extends OrderJpaRepository{

	@Lock(LockModeType.OPTIMISTIC)
	@Query("select o from Order o where o.id = :orderId")
	Optional<Order> findByIdForUpdate(Long orderId);
}
