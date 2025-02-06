package io.hhplus.ecommerce.global.lock;

import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import io.hhplus.ecommerce.global.exception.EcommerceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class DistributedLockTemplate implements LockTemplate {

	private final RedissonClient redisson;

	@Override
	public <T> T execute(final LockInfo lockInfo, final Supplier<T> task) {
		final RLock lock = redisson.getLock(lockInfo.key());

		try {
			final boolean isLocked = lock.tryLock(lockInfo.waitTime(), lockInfo.leaseTime(), lockInfo.timeUnit());
			if (!isLocked) {
				log.error("락 획득 실패");
				throw new EcommerceException("");
			}
			return task.get();
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Interrupted while waiting for lock", e);
			throw new EcommerceException("");
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	@Override
	public <T> T execute(final LockInfo lockInfo, final Supplier<T> task, final Supplier<T> fallback) {
		final RLock lock = redisson.getLock(lockInfo.key());

		try {
			final boolean isLocked = lock.tryLock(lockInfo.waitTime(), lockInfo.leaseTime(), lockInfo.timeUnit());
			if (!isLocked) {
				log.error("락 획득 실패로 fallback");
				return fallback.get();
			}
			return task.get();
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Interrupted while waiting for lock", e);
			return fallback.get();
		} finally {
			if (lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
}
