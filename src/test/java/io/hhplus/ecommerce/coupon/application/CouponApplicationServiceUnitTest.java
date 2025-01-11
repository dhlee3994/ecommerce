package io.hhplus.ecommerce.coupon.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.hhplus.ecommerce.coupon.application.request.CouponIssueRequest;
import io.hhplus.ecommerce.coupon.application.response.CouponResponse;
import io.hhplus.ecommerce.coupon.application.response.IssuedCouponResponse;
import io.hhplus.ecommerce.coupon.domain.Coupon;
import io.hhplus.ecommerce.coupon.domain.CouponIssuer;
import io.hhplus.ecommerce.coupon.domain.CouponQuantity;
import io.hhplus.ecommerce.coupon.domain.CouponQuantityRepository;
import io.hhplus.ecommerce.coupon.domain.CouponRepository;
import io.hhplus.ecommerce.coupon.domain.IssuedCoupon;
import io.hhplus.ecommerce.coupon.domain.IssuedCouponRepository;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.global.exception.InvalidRequestException;
import io.hhplus.ecommerce.user.domain.User;
import io.hhplus.ecommerce.user.domain.UserRepository;

@ExtendWith(MockitoExtension.class)
class CouponApplicationServiceUnitTest {

	@InjectMocks
	private CouponApplicationService couponApplicationService;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private CouponQuantityRepository couponQuantityRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CouponIssuer couponIssuer;

	@Mock
	private IssuedCouponRepository issuedCouponRepository;

	@DisplayName("쿠폰 단건 조회")
	@Nested
	class getCoupon {

		@DisplayName("쿠폰 아이디로 쿠폰을 조회할 수 있다.")
		@Test
		void getCouponById() throws Exception {
			// given
			final Long id = 1L;

			final String name = "쿠폰1";
			final int issueLimit = 30;
			final int quantity = 30;
			final int discountAmount = 1000;

			final Coupon coupon = Coupon.builder()
				.id(id)
				.name(name)
				.issueLimit(issueLimit)
				.quantity(quantity)
				.discountAmount(discountAmount)
				.build();

			given(couponRepository.findById(id))
				.willReturn(Optional.of(coupon));

			// when
			final CouponResponse result = couponApplicationService.getCoupon(id);

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

			given(couponRepository.findById(id))
				.willReturn(Optional.empty());

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
			final Long userId = 1L;
			final User user = User.builder()
				.name("홍길동")
				.build();

			given(userRepository.findById(userId))
				.willReturn(Optional.of(user));

			final String couponName = "쿠폰1";
			final int issueLimit = 30;
			final int quantity = 30;
			final int discountAmount = 1000;

			final Long couponId = 1L;
			final Coupon coupon = Coupon.builder()
				.id(couponId)
				.name(couponName)
				.issueLimit(issueLimit)
				.quantity(quantity)
				.discountAmount(discountAmount)
				.build();

			given(couponRepository.findById(anyLong()))
				.willReturn(Optional.of(coupon));

			final CouponQuantity couponQuantity = CouponQuantity.builder()
				.couponId(couponId)
				.quantity(quantity)
				.build();

			given(couponQuantityRepository.findByCouponIdForUpdate(couponId))
				.willReturn(Optional.of(couponQuantity));

			final CouponIssueRequest request = CouponIssueRequest.builder()
				.couponId(couponId)
				.userId(userId)
				.build();

			final IssuedCoupon issuedCoupon = IssuedCoupon.builder()
				.userId(user.getId())
				.couponId(coupon.getId())
				.expiredAt(coupon.getExpiredAt())
				.build();

			given(couponIssuer.issue(user, coupon, couponQuantity))
				.willReturn(issuedCoupon);

			given(issuedCouponRepository.save(issuedCoupon))
				.willReturn(issuedCoupon);

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
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			final CouponIssueRequest request = CouponIssueRequest.builder()
				.couponId(1L)
				.userId(1L)
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
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(User.builder().build()));

			given(couponRepository.findById(anyLong()))
				.willReturn(Optional.empty());

			final CouponIssueRequest request = CouponIssueRequest.builder()
				.couponId(1L)
				.userId(1L)
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
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(User.builder().build()));

			given(couponRepository.findById(anyLong()))
				.willReturn(Optional.of(Coupon.builder().build()));

			given(couponQuantityRepository.findByCouponIdForUpdate(anyLong()))
				.willReturn(Optional.empty());

			final CouponIssueRequest request = CouponIssueRequest.builder()
				.couponId(1L)
				.userId(1L)
				.build();

			// when & then
			assertThatThrownBy(() -> couponApplicationService.issueCoupon(request))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorCode.COUPON_NOT_FOUND.getMessage());
		}

		@DisplayName("쿠폰 발급 가능 수량을 초과해서 발급요청을 하면 EcommerceException 예외가 발생한다.")
		@Test
		void issueOverLimit() {
			// given
			final User user = User.builder().build();
			given(userRepository.findById(anyLong()))
				.willReturn(Optional.of(user));

			final long couponId = 1L;
			final int quantity = 0;
			final Coupon coupon = Coupon.builder()
				.id(couponId)
				.quantity(quantity)
				.build();
			given(couponRepository.findById(anyLong()))
				.willReturn(Optional.of(coupon));

			final CouponQuantity couponQuantity = CouponQuantity.builder()
				.couponId(couponId)
				.quantity(quantity)
				.build();
			given(couponQuantityRepository.findByCouponIdForUpdate(anyLong()))
				.willReturn(Optional.of(couponQuantity));

			willThrow(new EcommerceException(ErrorCode.COUPON_QUANTITY_IS_NOT_ENOUGH))
				.given(couponIssuer)
				.issue(user, coupon, couponQuantity);

			final CouponIssueRequest request = CouponIssueRequest.builder()
				.userId(1L)
				.couponId(1L)
				.build();

			// when & then
			assertThatThrownBy(() -> couponApplicationService.issueCoupon(request))
				.isInstanceOf(EcommerceException.class)
				.hasMessage(ErrorCode.COUPON_QUANTITY_IS_NOT_ENOUGH.getMessage());
		}
	}
}
