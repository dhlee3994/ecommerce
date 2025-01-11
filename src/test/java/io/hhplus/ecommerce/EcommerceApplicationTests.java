package io.hhplus.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("testcontainers")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class EcommerceApplicationTests {

	@Test
	void contextLoads() {
	}

}
