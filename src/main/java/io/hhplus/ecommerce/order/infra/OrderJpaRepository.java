package io.hhplus.ecommerce.order.infra;

import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.order.domain.Order;

@Profile("!optimistic-lock & !pessimistic-lock")
public interface OrderJpaRepository extends JpaRepository<Order, Long> {

	@Query("select o from Order o where o.id = :orderId")
	Optional<Order> findByIdForUpdate(Long orderId);
}
