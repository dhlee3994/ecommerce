package io.hhplus.ecommerce.product.application;

import static io.hhplus.ecommerce.global.exception.ErrorCode.PRODUCT_NOT_FOUND;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.global.exception.InvalidRequestException;
import io.hhplus.ecommerce.product.application.request.ProductSearchRequest;
import io.hhplus.ecommerce.product.application.response.BestProductResponse;
import io.hhplus.ecommerce.product.application.response.ProductResponse;
import io.hhplus.ecommerce.product.domain.Product;
import io.hhplus.ecommerce.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductApplicationService {

	private final ProductRepository productRepository;

	public Page<ProductResponse> getProducts(final ProductSearchRequest request, final Pageable pageable) {
		Page<Product> products = productRepository.getProducts(request.toSearchSpec(), pageable);
		return products.map(ProductResponse::from);
	}

	public ProductResponse getProduct(final Long id) {
		if (id == null || id <= 0) {
			throw new InvalidRequestException(ErrorCode.PRODUCT_ID_SHOULD_BE_POSITIVE);
		}

		final Product product = productRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(PRODUCT_NOT_FOUND.getMessage()));

		return ProductResponse.from(product);
	}
}
