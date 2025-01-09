package io.hhplus.ecommerce.product.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.hhplus.ecommerce.product.domain.spec.ProductSearchSpec;

public interface ProductRepository {

	Page<Product> getProducts(ProductSearchSpec spec, Pageable pageable);

	Optional<Product> findById(long id);

	List<Product> getAllById(List<Long> productIds);
}
