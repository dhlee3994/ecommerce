package io.hhplus.ecommerce.coupon.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.ecommerce.coupon.domain.CouponIssueToken;

@ExtendWith(MockitoExtension.class)
class CouponRedisCacheRepositoryUnitTest {

	@InjectMocks
	private CouponRedisCacheRepository couponRedisCacheRepository;

	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private ValueOperations<String, String> valueOps;

	@Mock
	private SetOperations<String, String> setOps;

	@Mock
	private ZSetOperations<String, String> zSetOps;

	@DisplayName("남은 쿠폰 개수를 조회할 수 있다.")
	@Test
	void getRemainingCouponCount() throws Exception {
		// given
		final long remainingCount = 10;

		final long couponId = 1L;
		final String couponKey = couponRedisCacheRepository.getCouponKeyFormat().formatted(couponId);
		given(redisTemplate.opsForValue()).willReturn(valueOps);
		given(valueOps.get(couponKey)).willReturn(String.valueOf(remainingCount));

		// when
		final long result = couponRedisCacheRepository.getRemainingCouponCount(couponId);

		// then
		assertThat(result).isEqualTo(remainingCount);
	}

	@DisplayName("유저가 이미 발급받은 쿠폰인지 확인할 수 있다.")
	@Test
	void isAlreadyIssue() throws Exception {
		// given
		final long userId = 1L;
		final long couponId = 1L;
		final String issuedUserKey = couponRedisCacheRepository.getCouponIssuedUserKeyFormat().formatted(couponId);
		given(redisTemplate.opsForSet()).willReturn(setOps);
		given(setOps.isMember(issuedUserKey, String.valueOf(userId))).willReturn(true);

		// when
		final boolean result = couponRedisCacheRepository.isAlreadyIssue(userId, couponId);

		// then
		assertThat(result).isTrue();
	}

	@DisplayName("쿠폰 발급 큐에 토큰을 추가할 수 있다.")
	@Test
	void addCouponQueue() throws Exception {
		// given
		final CouponIssueToken token = new CouponIssueToken(1L, 1L);
		final String tokenJson = "{\"userId\":1,\"couponId\":1}";
		given(objectMapper.writeValueAsString(token)).willReturn(tokenJson);
		given(redisTemplate.opsForZSet()).willReturn(zSetOps);
		given(zSetOps.add(anyString(), anyString(), anyDouble()))
			.willReturn(Boolean.TRUE);

		// when
		final boolean result = couponRedisCacheRepository.addCouponQueue(token);

		// then
		assertThat(result).isTrue();
	}

	@DisplayName("쿠폰 발급 큐에서 토큰 목록을 가져올 수 있다.")
	@Test
	void getCouponIssueTokens() throws Exception {
		// given
		final int count = 1;
		final CouponIssueToken token = new CouponIssueToken(1L, 1L);
		final String tokenJson = "{\"userId\":1,\"couponId\":1}";

		given(objectMapper.readValue(tokenJson, CouponIssueToken.class)).willReturn(token);
		given(redisTemplate.opsForZSet()).willReturn(zSetOps);

		final ZSetOperations.TypedTuple<String> tuple = mock(ZSetOperations.TypedTuple.class);
		given(tuple.getValue()).willReturn(tokenJson);

		final Set<ZSetOperations.TypedTuple<String>> tupleSet = new HashSet<>();
		tupleSet.add(tuple);

		given(zSetOps.popMin(couponRedisCacheRepository.getCouponIssuedQueueKey(), count)).willReturn(tupleSet);
		given(objectMapper.readValue(tokenJson, CouponIssueToken.class)).willReturn(token);

		// when
		final Set<CouponIssueToken> result = couponRedisCacheRepository.getCouponIssueTokens(count);

		// then
		assertThat(result).hasSize(count)
			.containsExactly(token);
	}

	@DisplayName("쿠폰 수량을 감소시킬 수 있다.")
	@Test
	void decreaseCouponCount() throws Exception {
		// given
		final long couponId = 1L;
		final String couponKey = couponRedisCacheRepository.getCouponKeyFormat().formatted(couponId);
		given(redisTemplate.opsForValue()).willReturn(valueOps);

		// when
		couponRedisCacheRepository.decreaseCouponCount(couponId);

		// then
		verify(valueOps).decrement(couponKey);
	}

	@DisplayName("쿠폰 발급 큐에서 토큰을 삭제할 수 있다.")
	@Test
	void removeCouponIssueToken() throws Exception {
		// given
		final CouponIssueToken token = new CouponIssueToken(1L, 1L);
		final String tokenJson = "{\"userId\":1,\"couponId\":1}";
		given(objectMapper.writeValueAsString(token)).willReturn(tokenJson);
		given(redisTemplate.opsForZSet()).willReturn(zSetOps);

		// when
		couponRedisCacheRepository.removeCouponIssueToken(token);

		// then
		verify(zSetOps).remove(couponRedisCacheRepository.getCouponIssuedQueueKey(), tokenJson);
	}
}
