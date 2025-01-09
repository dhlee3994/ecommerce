package io.hhplus.ecommerce.payment.infra;

import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.payment.domain.Payment;
import io.hhplus.ecommerce.payment.domain.PaymentRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

	private final PaymentJpaRepository paymentJpaRepository;

	@Override
	public Payment save(final Payment payment) {
		return paymentJpaRepository.save(payment);
	}
}
