package io.hhplus.ecommerce.product.domain;

import org.springframework.stereotype.Service;

@Service
public class StockService {

	public void decrease(final Product product, final Stock stock, final int quantity) {
		stock.decrease(quantity);
		product.updateQuantity(stock.getQuantity());
	}
}
