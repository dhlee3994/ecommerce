package io.hhplus.ecommerce.order.application;

import static io.hhplus.ecommerce.global.exception.ErrorCode.PRODUCT_NOT_FOUND;
import static io.hhplus.ecommerce.global.exception.ErrorCode.USER_NOT_FOUND;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.hhplus.ecommerce.order.application.request.OrderCreateRequest;
import io.hhplus.ecommerce.order.application.response.OrderCreateResponse;
import io.hhplus.ecommerce.order.domain.Order;
import io.hhplus.ecommerce.order.domain.OrderItem;
import io.hhplus.ecommerce.order.domain.OrderItemRepository;
import io.hhplus.ecommerce.order.domain.OrderRepository;
import io.hhplus.ecommerce.product.domain.Product;
import io.hhplus.ecommerce.product.domain.ProductRepository;
import io.hhplus.ecommerce.product.domain.Stock;
import io.hhplus.ecommerce.product.domain.StockRepository;
import io.hhplus.ecommerce.product.domain.StockService;
import io.hhplus.ecommerce.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderApplicationService {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;

	private final ProductRepository productRepository;
	private final StockRepository stockRepository;

	private final UserRepository userRepository;

	private final StockService stockService;

	@Transactional
	public OrderCreateResponse order(final OrderCreateRequest request) {
		if (!userRepository.existsById(request.getUserId())) {
			throw new EntityNotFoundException(USER_NOT_FOUND.getMessage());
		}

		Order order = orderRepository.save(Order.createOrder(request.getUserId()));

		final List<Long> productIds = request.extractProductIds();
		final List<Product> products = productRepository.getAllById(productIds);
		if (products.size() != productIds.size()) {
			throw new EntityNotFoundException(PRODUCT_NOT_FOUND.getMessage());
		}
		final Map<Long, Product> productMap = products.stream()
			.collect(Collectors.toMap(Product::getId, Function.identity()));

		request.getOrderItems()
			.forEach(orderItem -> {

				final Product product = productMap.get(orderItem.getProductId());
				final Stock stock = stockRepository.findByProductIdForUpdate(product.getId())
					.orElseThrow(() -> new EntityNotFoundException(PRODUCT_NOT_FOUND.getMessage()));

				stockService.decrease(product, stock, orderItem.getQuantity());

				final OrderItem item = orderItemRepository.save(OrderItem.builder()
					.orderId(order.getId())
					.productId(product.getId())
					.productName(product.getName())
					.quantity(orderItem.getQuantity())
					.price(product.getPrice())
					.build());
				order.addOrderItem(item);
			});
		return OrderCreateResponse.from(order);
	}
}
