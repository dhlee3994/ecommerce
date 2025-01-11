package io.hhplus.ecommerce.product.infra;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.hhplus.ecommerce.product.domain.Product;
import io.hhplus.ecommerce.product.domain.spec.ProductSearchSpec;

@Component
public class ProductSpecGenerator {

	public Specification<Product> searchWith(final ProductSearchSpec spec) {
		return ((root, query, builder) -> {
			final ArrayList<Predicate> predicates = new ArrayList<>();
			if (StringUtils.hasText(spec.getName())) {
				predicates.add(builder.like(root.get("name"), "%" + spec.getName() + "%"));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		});
	}
}
