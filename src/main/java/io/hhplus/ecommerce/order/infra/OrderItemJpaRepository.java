package io.hhplus.ecommerce.order.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import io.hhplus.ecommerce.order.domain.OrderItem;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
}
