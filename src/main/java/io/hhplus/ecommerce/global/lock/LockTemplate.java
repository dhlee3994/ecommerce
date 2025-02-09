package io.hhplus.ecommerce.global.lock;

import java.util.function.Supplier;

public interface LockTemplate {

	<T> T execute(LockInfo lockInfo, Supplier<T> task);

	<T> T execute(LockInfo lockInfo, Supplier<T> task, Supplier<T> fallback);
}
