package io.hhplus.ecommerce.product.infra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import io.hhplus.ecommerce.product.domain.BestProduct;
import io.hhplus.ecommerce.product.domain.Product;
import io.hhplus.ecommerce.product.domain.ProductRepository;
import io.hhplus.ecommerce.product.domain.spec.ProductSearchSpec;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ProductRepositoryImpl implements ProductRepository {

	private final ProductJpaRepository productJpaRepository;
	private final ProductSpecGenerator productSpecGenerator;

	@Override
	public Page<Product> getProducts(final ProductSearchSpec spec, final Pageable pageable) {
		return productJpaRepository.findAll(productSpecGenerator.searchWith(spec), pageable);
	}

	@Override
	public Optional<Product> findById(final long id) {
		return productJpaRepository.findById(id);
	}

	@Override
	public List<Product> getAllById(final List<Long> productIds) {
		return productJpaRepository.findAllById(productIds);
	}

	@Override
	public List<BestProduct> getBestProducts(
		final LocalDateTime startDateTime,
		final LocalDateTime endDateTime,
		final Pageable pageable
	) {
		return productJpaRepository.getBestProducts(startDateTime, endDateTime, pageable);
	}
}
