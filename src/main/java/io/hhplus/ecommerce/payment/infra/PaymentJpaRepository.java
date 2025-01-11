package io.hhplus.ecommerce.payment.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import io.hhplus.ecommerce.payment.domain.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
