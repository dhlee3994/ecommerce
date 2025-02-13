package io.hhplus.ecommerce.payment.domain.event;

public interface PaymentCompletedEventListener {

	void publish(PaymentCompletedEvent paymentCompletedEvent);
}
