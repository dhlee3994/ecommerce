package io.hhplus.ecommerce.coupon.infra;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.hhplus.ecommerce.coupon.domain.CouponIssueToken;
import io.hhplus.ecommerce.coupon.domain.CouponPublishRepository;
import io.hhplus.ecommerce.global.exception.EcommerceException;
import io.hhplus.ecommerce.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RedisCouponPublishRepository implements CouponPublishRepository {

	private static final String COUPON_KEY_FORMAT = "coupon:issue:%d";
	private static final String COUPON_ISSUED_USER_KEY_FORMAT = "coupon:issue:%d:user";
	private static final String COUPON_ISSUED_QUEUE_KEY = "coupon:issue:queue";

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public long getRemainingCouponCount(final long couponId) {
		final String key = COUPON_KEY_FORMAT.formatted(couponId);
		final String value = redisTemplate.opsForValue().get(key);
		return value == null ? 0 : Long.parseLong(value);
	}

	@Override
	public boolean isAlreadyIssue(final long userId, final long couponId) {
		final String issuedUserKey = COUPON_ISSUED_USER_KEY_FORMAT.formatted(couponId);
		final Boolean member = redisTemplate.opsForSet().isMember(issuedUserKey, String.valueOf(userId));
		return Boolean.TRUE.equals(member);
	}

	@Override
	public boolean addCouponQueue(final CouponIssueToken token) {
		try {
			final Boolean addSuccess = redisTemplate.opsForZSet()
				.add(COUPON_ISSUED_QUEUE_KEY, objectMapper.writeValueAsString(token), System.currentTimeMillis());

			return Boolean.TRUE.equals(addSuccess);
		} catch (final JsonProcessingException e) {
			log.error("쿠폰 발급 큐 저장 에러", e);
			throw new EcommerceException(ErrorCode.COUPON_ISSUE_TOKEN_PARSING_ERROR);
		}
	}

	@Override
	public Set<CouponIssueToken> getCouponIssueTokens(final int count) {
		final var tokens = redisTemplate.opsForZSet()
			.popMin(COUPON_ISSUED_QUEUE_KEY, count);

		if (ObjectUtils.isEmpty(tokens)) {
			return Set.of();
		}

		return tokens.stream()
			.map(token -> {
				try {
					return objectMapper.readValue(token.getValue(), CouponIssueToken.class);
				} catch (final JsonProcessingException e) {
					log.error("쿠폰 발급 큐 팝(pop) 에러", e);
					throw new EcommerceException(ErrorCode.COUPON_ISSUE_TOKEN_PARSING_ERROR);
				}
			})
			.collect(Collectors.toSet());

	}

	@Override
	public void decreaseCouponCount(final long couponId) {
		final String key = COUPON_KEY_FORMAT.formatted(couponId);
		redisTemplate.opsForValue().decrement(key);
	}

	@Override
	public void addIssuedUser(final long userId, final long couponId) {
		final String issuedUserKey = COUPON_ISSUED_USER_KEY_FORMAT.formatted(couponId);
		redisTemplate.opsForSet().add(issuedUserKey, String.valueOf(userId));
	}

	@Override
	public void removeCouponIssueToken(final CouponIssueToken token) {
		try {
			redisTemplate.opsForZSet()
				.remove(COUPON_ISSUED_QUEUE_KEY, objectMapper.writeValueAsString(token));
		} catch (final JsonProcessingException e) {
			log.error("쿠폰 발급 큐 삭제 에러", e);
			throw new EcommerceException(ErrorCode.COUPON_ISSUE_TOKEN_PARSING_ERROR);
		}
	}

	public String getCouponKeyFormat() {
		return COUPON_KEY_FORMAT;
	}

	public String getCouponIssuedUserKeyFormat() {
		return COUPON_ISSUED_USER_KEY_FORMAT;
	}

	public String getCouponIssuedQueueKey() {
		return COUPON_ISSUED_QUEUE_KEY;
	}
}
