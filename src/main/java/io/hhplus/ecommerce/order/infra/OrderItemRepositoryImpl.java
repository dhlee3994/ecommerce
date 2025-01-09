package io.hhplus.ecommerce.order.infra;

import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.order.domain.OrderItem;
import io.hhplus.ecommerce.order.domain.OrderItemRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class OrderItemRepositoryImpl implements OrderItemRepository {

	private final OrderItemJpaRepository orderItemJpaRepository;

	@Override
	public OrderItem save(final OrderItem orderItem) {
		return orderItemJpaRepository.save(orderItem);
	}
}
