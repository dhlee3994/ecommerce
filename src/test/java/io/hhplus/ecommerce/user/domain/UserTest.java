package io.hhplus.ecommerce.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

	@DisplayName("사용자의 이름으로 User 객체를 생성할 수 있다.")
	@Test
	void createUser() throws Exception {
		// given
		final String name = "항해플러스";

		// when
		final User result = User.builder()
			.name(name)
			.build();

		// then
		assertThat(result.getName()).isEqualTo(name);
	}
}
