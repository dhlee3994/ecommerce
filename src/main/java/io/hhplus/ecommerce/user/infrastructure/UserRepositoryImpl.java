package io.hhplus.ecommerce.user.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.user.domain.User;
import io.hhplus.ecommerce.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	public Optional<User> findById(final long userId) {
		return userJpaRepository.findById(userId);
	}

	@Override
	public boolean existsById(long userId) {
		return userJpaRepository.existsById(userId);
	}
}
