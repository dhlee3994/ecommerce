package io.hhplus.ecommerce.order.infra;

import jakarta.persistence.LockModeType;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.order.domain.Order;

@Profile("pessimistic-lock")
public interface PessimisticOrderJpaRepository extends OrderJpaRepository{

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select o from Order o where o.id = :orderId")
	Optional<Order> findByIdForUpdate(Long orderId);
}
