package io.hhplus.ecommerce.payment.domain;

public interface DataPlatformClient {

	void sendOrderData(OrderData orderData);
}
