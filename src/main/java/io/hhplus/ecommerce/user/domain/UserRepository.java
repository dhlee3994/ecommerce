package io.hhplus.ecommerce.user.domain;

import java.util.Optional;

public interface UserRepository {

	Optional<User> findById(long userId);

	boolean existsById(long userId);
}
