package io.hhplus.ecommerce.payment.domain.event;

public interface PaymentCompletedEventListener {

	void saveOutbox(PaymentCompletedEvent paymentCompletedEvent);

	void sendMessage(PaymentCompletedEvent paymentCompletedEvent);
}
