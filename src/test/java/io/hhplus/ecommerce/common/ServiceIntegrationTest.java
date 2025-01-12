package io.hhplus.ecommerce.common;

import org.springframework.beans.factory.annotation.Autowired;

import io.hhplus.ecommerce.coupon.application.CouponApplicationService;
import io.hhplus.ecommerce.coupon.infra.CouponJpaRepository;
import io.hhplus.ecommerce.coupon.infra.CouponQuantityJpaRepository;
import io.hhplus.ecommerce.coupon.infra.IssuedCouponJpaRepository;
import io.hhplus.ecommerce.order.application.OrderApplicationService;
import io.hhplus.ecommerce.order.infra.OrderItemJpaRepository;
import io.hhplus.ecommerce.order.infra.OrderJpaRepository;
import io.hhplus.ecommerce.payment.application.PaymentApplicationService;
import io.hhplus.ecommerce.point.application.PointApplicationService;
import io.hhplus.ecommerce.point.infra.PointJpaRepository;
import io.hhplus.ecommerce.product.application.ProductApplicationService;
import io.hhplus.ecommerce.product.infra.ProductJpaRepository;
import io.hhplus.ecommerce.product.infra.StockJpaRepository;
import io.hhplus.ecommerce.user.infrastructure.UserJpaRepository;

public abstract class ServiceIntegrationTest extends IntegrationTest {

	@Autowired
	protected CouponApplicationService couponApplicationService;

	@Autowired
	protected OrderApplicationService orderApplicationService;

	@Autowired
	protected PaymentApplicationService paymentApplicationService;

	@Autowired
	protected PointApplicationService pointApplicationService;

	@Autowired
	protected ProductApplicationService productApplicationService;

	// coupon
	@Autowired
	protected CouponJpaRepository couponJpaRepository;
	@Autowired
	protected IssuedCouponJpaRepository issuedCouponJpaRepository;
	@Autowired
	protected CouponQuantityJpaRepository couponQuantityJpaRepository;

	// order
	@Autowired
	protected OrderJpaRepository orderJpaRepository;
	@Autowired
	protected OrderItemJpaRepository orderItemJpaRepository;

	// product
	@Autowired
	protected ProductJpaRepository productJpaRepository;
	@Autowired
	protected StockJpaRepository stockJpaRepository;

	// point
	@Autowired
	protected PointJpaRepository pointJpaRepository;

	// user
	@Autowired
	protected UserJpaRepository userJpaRepository;


}
