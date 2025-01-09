package io.hhplus.ecommerce.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import io.hhplus.ecommerce.user.domain.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
