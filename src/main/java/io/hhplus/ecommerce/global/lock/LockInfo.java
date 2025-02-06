package io.hhplus.ecommerce.global.lock;

import java.util.concurrent.TimeUnit;

public record LockInfo(
	String key,
	long waitTime,
	long leaseTime,
	TimeUnit timeUnit
) {

	/**
	 * @param key: Lock key
	 * @param waitTime: Lock WaitTime(seconds)
	 * @param leaseTime: Lock LeaseTime(seconds)
	 */
	public LockInfo(String key, long waitTime, long leaseTime) {
		this(key, waitTime, leaseTime, TimeUnit.SECONDS);
	}

	/**
	 * WaitTime: 5 seconds<br/>
	 * LeaseTime: 5 seconds<br/>
	 * @param key: Lock Key
	 */
	public LockInfo(String key) {
		this(key, 5, 5, TimeUnit.SECONDS);
	}
}
