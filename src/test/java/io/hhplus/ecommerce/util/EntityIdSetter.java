package io.hhplus.ecommerce.util;

import org.springframework.test.util.ReflectionTestUtils;

public class EntityIdSetter {

	public static void setId(Object entity, Long id) {
		ReflectionTestUtils.setField(entity, "id", id);
	}
}
