package io.hhplus.ecommerce.order.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

	private final OrderJpaRepository orderJpaRepository;

	@Override
	public Order save(final Order order) {
		return orderJpaRepository.save(order);
	}

	@Override
	public Optional<Order> findByIdForUpdate(final Long id) {
		return orderJpaRepository.findByIdForUpdate(id);
	}
}
