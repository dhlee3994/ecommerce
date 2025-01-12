package io.hhplus.ecommerce.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.Type;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataCleaner {

	private final EntityManager em;
	private final List<String> tableNames;

	public DataCleaner(final EntityManager em) {
		this.em = em;
		this.tableNames = em.getMetamodel()
			.getEntities()
			.stream()
			.map(Type::getJavaType)
			.map(javaType -> javaType.getAnnotation(Table.class))
			.map(Table::name)
			.toList();
	}

	@Transactional
	public void clean() {
		em.flush();
		tableNames.forEach(this::truncateTable);
	}

	private int truncateTable(final String tableName) {
		return em.createNativeQuery("truncate table " + tableName)
			.executeUpdate();
	}
}
