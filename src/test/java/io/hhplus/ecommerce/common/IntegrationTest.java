package io.hhplus.ecommerce.common;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import io.hhplus.ecommerce.util.DataCleaner;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
public abstract class IntegrationTest {

	private static final MySQLContainer<?> mysqlContainer;
	private static final GenericContainer<?> redisContainer;

	static {
		mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.4.3"))
			.withDatabaseName("ecommerce")
			.withUsername("ecommerce")
			.withPassword("aa")
			.withEnv("TZ", "UTC")
			.withInitScript("init.sql")
			.withReuse(true);

		redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.4.2"))
			.withExposedPorts(6379)
			.withReuse(true);

		mysqlContainer.start();
		redisContainer.start();
	}

	@DynamicPropertySource
	static void properties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mysqlContainer::getUsername);
		registry.add("spring.datasource.password", mysqlContainer::getPassword);

		registry.add("spring.data.redis.host", redisContainer::getHost);
		registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
	}

	@Autowired
	private DataCleaner dataCleaner;

	@BeforeEach
	void setUp() {
		dataCleaner.clean();
	}
}
