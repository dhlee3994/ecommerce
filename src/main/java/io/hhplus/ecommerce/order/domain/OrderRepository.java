package io.hhplus.ecommerce.order.domain;

import java.util.Optional;

public interface OrderRepository {

	Order save(Order order);

	Optional<Order> findByIdForUpdate(Long id);
}
