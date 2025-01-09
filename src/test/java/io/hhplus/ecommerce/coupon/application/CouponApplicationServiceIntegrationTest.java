package io.hhplus.ecommerce.coupon.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.utility.TestcontainersConfiguration;

import io.hhplus.ecommerce.coupon.application.request.CouponIssueRequest;
import io.hhplus.ecommerce.coupon.application.response.CouponResponse;
import io.hhplus.ecommerce.coupon.application.response.IssuedCouponResponse;
import io.hhplus.ecommerce.coupon.domain.Coupon;
import io.hhplus.ecommerce.coupon.domain.CouponQuantity;
import io.hhplus.ecommerce.coupon.infra.CouponJpaRepository;
import io.hhplus.ecommerce.coupon.infra.CouponQuantityJpaRepository;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.global.exception.InvalidRequestException;
import io.hhplus.ecommerce.user.domain.User;
import io.hhplus.ecommerce.user.infrastructure.UserJpaRepository;

@ActiveProfiles("testcontainers")
@ImportTestcontainers(TestcontainersConfiguration.class)
@SpringBootTest
class CouponApplicationServiceIntegrationTest {

	@Autowired
	private CouponApplicationService couponApplicationService;

	@Autowired
	private CouponJpaRepository couponJpaRepository;

	@Autowired
	private CouponQuantityJpaRepository couponQuantityJpaRepository;

	@Autowired
	private UserJpaRepository userJpaRepository;

	@AfterEach
	void tearDown() {
		couponJpaRepository.deleteAllInBatch();
		couponQuantityJpaRepository.deleteAllInBatch();
		userJpaRepository.deleteAllInBatch();
	}

	@DisplayName("쿠폰 단건 조회")
	@Nested
	class getCoupon {

		@DisplayName("쿠폰 아이디로 쿠폰을 조회할 수 있다.")
		@Test
		void getCouponById() throws Exception {
			// given
			final String name = "쿠폰1";
			final int issueLimit = 30;
			final int quantity = 30;
			final int discountAmount = 1000;

			final Coupon savedCoupon = couponJpaRepository.save(Coupon.builder()
				.name(name)
				.issueLimit(issueLimit)
				.quantity(quantity)
				.discountAmount(discountAmount)
				.build());

			// when
			final CouponResponse result = couponApplicationService.getCoupon(savedCoupon.getId());

			// then
			assertThat(result).isNotNull()
				.extracting("name", "issueLimit", "quantity", "discountAmount")
				.containsExactly(name, issueLimit, quantity, discountAmount);
		}

		@DisplayName("조회하려는 쿠폰 아이디가 Null이면 InvalidRequestException이 발생한다.")
		@Test
		void getCouponByNullId() throws Exception {
			// given
			final Long id = null;

			// when & then
			assertThatThrownBy(() -> couponApplicationService.getCoupon(id))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.COUPON_ID_SHOULD_BE_POSITIVE.getMessage());
		}

		@DisplayName("조회하려는 쿠폰 아이디가 0이면 InvalidRequestException이 발생한다.")
		@Test
		void getCouponByZeroId() {
			// given
			final Long id = 0L;

			// when & then
			assertThatThrownBy(() -> couponApplicationService.getCoupon(id))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.COUPON_ID_SHOULD_BE_POSITIVE.getMessage());
		}

		@DisplayName("조회하려는 쿠폰 아이디가 음수면 InvalidRequestException이 발생한다.")
		@Test
		void getCouponByNegativeId() {
			// given
			final Long id = -1L;

			// when & then
			assertThatThrownBy(() -> couponApplicationService.getCoupon(id))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.COUPON_ID_SHOULD_BE_POSITIVE.getMessage());
		}

		@DisplayName("쿠폰 아이디로 조회할 때 쿠폰이 존재하지 않으면 EntityNotFoundException이 발생한다.")
		@Test
		void getCouponByIdNotFound() {
			// given
			final Long id = 1L;

			// when & then
			assertThatThrownBy(() -> couponApplicationService.getCoupon(id))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorCode.COUPON_NOT_FOUND.getMessage());
		}
	}

	@DisplayName("쿠폰 발급")
	@Nested
	class issueCoupon {

		@DisplayName("쿠폰 발급을 성공한다.")
		@Test
		void issueCoupon() throws Exception {
			// given
			final String userName = "사용자1";
			final User user = userJpaRepository.save(User.builder().name(userName).build());

			final String couponName = "쿠폰1";
			final int issueLimit = 30;
			final int quantity = 30;
			final int discountAmount = 1000;

			final Coupon coupon = couponJpaRepository.save(Coupon.builder()
				.name(couponName)
				.issueLimit(issueLimit)
				.quantity(quantity)
				.discountAmount(discountAmount)
				.build());

			final CouponQuantity couponQuantity = couponQuantityJpaRepository.save(CouponQuantity.builder()
				.couponId(coupon.getId())
				.quantity(quantity)
				.build());

			final CouponIssueRequest request = CouponIssueRequest.builder()
				.couponId(coupon.getId())
				.userId(user.getId())
				.build();

			// when
			final IssuedCouponResponse result = couponApplicationService.issueCoupon(request);

			// then
			assertThat(result).isNotNull()
				.extracting("couponId", "name", "discountAmount")
				.containsExactly(coupon.getId(), couponName, discountAmount);

			assertThat(coupon.getQuantity()).isEqualTo(couponQuantity.getQuantity());
		}

		@DisplayName("유효하지 않은 사용자 아이디로 요청하면 쿠폰 발급을 요청하면 EntityNotFoundException 예외가 발생한다.")
		@Test
		void issueWithNotFoundUser() {
			// given
			final Coupon coupon = couponJpaRepository.save(Coupon.builder()
				.name("쿠폰1")
				.issueLimit(30)
				.quantity(30)
				.discountAmount(1000)
				.build());

			final CouponIssueRequest request = CouponIssueRequest.builder()
				.couponId(coupon.getId())
				.userId(System.currentTimeMillis())
				.build();

			// when & then
			assertThatThrownBy(() -> couponApplicationService.issueCoupon(request))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
		}

		@DisplayName("유효하지 않은 쿠폰 아이디로 요청하면 쿠폰 발급을 요청하면 EntityNotFoundException 예외가 발생한다.")
		@Test
		void issueWithNotFoundCoupon() {
			// given
			final User user = userJpaRepository.save(User.builder().name("사용자1").build());

			final CouponIssueRequest request = CouponIssueRequest.builder()
				.userId(user.getId())
				.couponId(System.currentTimeMillis())
				.build();

			// when & then
			assertThatThrownBy(() -> couponApplicationService.issueCoupon(request))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorCode.COUPON_NOT_FOUND.getMessage());
		}

		@DisplayName("유효하지 않은 쿠폰 수량 아이디로 요청하면 쿠폰 발급을 요청하면 EntityNotFoundException 예외가 발생한다.")
		@Test
		void issueWithNotFoundCouponQuantity() {
			// given
			final User user = userJpaRepository.save(User.builder().name("사용자1").build());
			final Coupon coupon = couponJpaRepository.save(Coupon.builder()
				.name("쿠폰1")
				.issueLimit(30)
				.quantity(30)
				.discountAmount(1000)
				.build());

			final CouponIssueRequest request = CouponIssueRequest.builder()
				.userId(user.getId())
				.couponId(coupon.getId())
				.build();

			// when & then
			assertThatThrownBy(() -> couponApplicationService.issueCoupon(request))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorCode.COUPON_NOT_FOUND.getMessage());
		}

		@DisplayName("발급 가능한 쿠폰이 0이하시 발급요청을 하면 EcommerceException 예외가 발생한다.")
		@Test
		void issueCouponQuantityNotEnough() throws Exception {
			// given
			final String userName = "사용자1";
			final User user = userJpaRepository.save(User.builder().name(userName).build());

			final String couponName = "쿠폰1";
			final int issueLimit = 30;
			final int quantity = 0;
			final int discountAmount = 1000;

			final Coupon coupon = couponJpaRepository.save(Coupon.builder()
				.name(couponName)
				.issueLimit(issueLimit)
				.quantity(quantity)
				.discountAmount(discountAmount)
				.build());

			couponQuantityJpaRepository.save(CouponQuantity.builder()
				.couponId(coupon.getId())
				.quantity(quantity)
				.build());

			final CouponIssueRequest request = CouponIssueRequest.builder()
				.userId(user.getId())
				.couponId(coupon.getId())
				.build();

			// when & then
			assertThatThrownBy(() -> couponApplicationService.issueCoupon(request))
				.isInstanceOf(EcommerceException.class)
				.hasMessage(ErrorCode.COUPON_QUANTITY_IS_NOT_ENOUGH.getMessage());
		}
	}
}
