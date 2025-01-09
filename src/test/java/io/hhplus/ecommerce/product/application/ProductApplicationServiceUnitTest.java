package io.hhplus.ecommerce.product.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.hhplus.ecommerce.global.exception.ErrorCode;
import io.hhplus.ecommerce.global.exception.InvalidRequestException;
import io.hhplus.ecommerce.product.application.request.ProductSearchRequest;
import io.hhplus.ecommerce.product.application.response.BestProductResponse;
import io.hhplus.ecommerce.product.application.response.ProductResponse;
import io.hhplus.ecommerce.product.domain.BestProduct;
import io.hhplus.ecommerce.product.domain.Product;
import io.hhplus.ecommerce.product.domain.ProductRepository;
import io.hhplus.ecommerce.product.domain.spec.ProductSearchSpec;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceUnitTest {

	@InjectMocks
	private ProductApplicationService productApplicationService;

	@Mock
	private ProductRepository productRepository;

	@DisplayName("상품 목록 조회")
	@Nested
	class getProducts {

		@DisplayName("상품명으로 상품 목록을 조회할 수 있다.")
		@Test
		void getProductsByName() {
			// given
			final long id = 1L;
			final String name = "상품1";
			final int price = 1000;
			final int quantity = 10;

			final ProductSearchRequest request = ProductSearchRequest.builder()
				.name(name)
				.build();

			final Pageable pageable = PageRequest.of(0, 10);

			final List<Product> content = List.of(
				Product.builder().id(id).name(name).price(price).quantity(quantity).build()
			);

			given(productRepository.getProducts(any(ProductSearchSpec.class), any(Pageable.class)))
				.willReturn(new PageImpl<>(content, pageable, 1));

			// when
			final Page<ProductResponse> result = productApplicationService.getProducts(request, pageable);

			// then
			assertThat(result.getContent()).hasSize(1)
				.extracting("id", "name", "price", "quantity")
				.containsExactly(
					tuple(id, name, price, quantity)
				);
			assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
			assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		}

		@DisplayName("조회 조건이 포함된 상품명의 상품 목록을 조회할 수 있다.")
		@Test
		void getProductsByNameLike() {
			// given
			final String name = "상품";

			final ProductSearchRequest request = ProductSearchRequest.builder()
				.name(name)
				.build();

			final Pageable pageable = PageRequest.of(0, 10);

			final List<Product> content = List.of(
				Product.builder().id(1L).name(name + "1").price(1000).quantity(10).build(),
				Product.builder().id(2L).name(name + "2").price(2000).quantity(20).build()
			);

			given(productRepository.getProducts(any(ProductSearchSpec.class), any(Pageable.class)))
				.willReturn(new PageImpl<>(content, pageable, 1));

			// when
			final Page<ProductResponse> result = productApplicationService.getProducts(request, pageable);

			// then
			assertThat(result.getContent()).hasSize(2)
				.extracting("id", "name", "price", "quantity")
				.containsExactlyInAnyOrder(
					tuple(1L, name + "1", 1000, 10),
					tuple(2L, name + "2", 2000, 20)
				);
			assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
			assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		}

		@DisplayName("조회 조건이 포함된 상품명을 가진 상품이 없으면 빈 리스트를 반환한다.")
		@Test
		void getProductsByNameNotMatch() {
			// given
			final String name = "상품";

			final ProductSearchRequest request = ProductSearchRequest.builder()
				.name(name)
				.build();

			final Pageable pageable = PageRequest.of(0, 10);

			final List<Product> content = List.of();

			given(productRepository.getProducts(any(ProductSearchSpec.class), any(Pageable.class)))
				.willReturn(new PageImpl<>(content, pageable, 1));

			// when
			final Page<ProductResponse> result = productApplicationService.getProducts(request, pageable);

			// then
			assertThat(result.getContent()).hasSize(0);
			assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
			assertThat(result.getPageable().getPageSize()).isEqualTo(10);
		}
	}

	@DisplayName("상품 단건 조회")
	@Nested
	class getProduct {

		@DisplayName("상품 아이디로 상품을 조회할 수 있다.")
		@Test
		void getProductById() {
			// given
			final long id = 1L;
			final String name = "상품1";
			final int price = 1000;
			final int quantity = 10;

			final Product product = Product.builder()
				.id(id)
				.name(name)
				.price(price)
				.quantity(quantity)
				.build();

			given(productRepository.findById(id))
				.willReturn(Optional.of(product));

			// when
			final ProductResponse result = productApplicationService.getProduct(id);

			// then
			assertThat(result).isNotNull()
				.extracting("name", "price", "quantity")
				.containsExactly(name, price, quantity);
		}

		@DisplayName("조회하려는 상품 아이디가 Null이면 InvalidRequestException이 발생한다.")
		@Test
		void getProductByNullId() {
			// given
			final Long id = null;

			// when & then
			assertThatThrownBy(() -> productApplicationService.getProduct(id))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.PRODUCT_ID_SHOULD_BE_POSITIVE.getMessage());
		}

		@DisplayName("조회하려는 상품 아이디가 0이면 InvalidRequestException이 발생한다.")
		@Test
		void getProductByZeroId() {
			// given
			final Long id = 0L;

			// when & then
			assertThatThrownBy(() -> productApplicationService.getProduct(id))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.PRODUCT_ID_SHOULD_BE_POSITIVE.getMessage());
		}

		@DisplayName("조회하려는 상품 아이디가 양수가 아니면 InvalidRequestException이 발생한다.")
		@Test
		void getProductByNegativeId() {
			// given
			final Long id = -1L;

			// when & then
			assertThatThrownBy(() -> productApplicationService.getProduct(id))
				.isInstanceOf(InvalidRequestException.class)
				.hasMessage(ErrorCode.PRODUCT_ID_SHOULD_BE_POSITIVE.getMessage());
		}

		@DisplayName("상품 아이디로 조회할 때 상품이 존재하지 않으면 EntityNotFoundException이 발생한다.")
		@Test
		void getProductByIdNotFound() {
			// given
			final Long id = 1L;

			given(productRepository.findById(id))
				.willReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> productApplicationService.getProduct(id))
				.isInstanceOf(EntityNotFoundException.class)
				.hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
		}
	}

	@DisplayName("인기 상품 조회")
	@Nested
	class getBestProducts {
		@DisplayName("판매량이 가장 많은 상품 5개를 조회할 수 있다.")
		@Test
		void getBestProducts() {
			// given
			List<BestProduct> bestProducts = List.of(
				BestProduct.builder().productId(1L).name("상품1").totalSaleCount(10L).build(),
				BestProduct.builder().productId(2L).name("상품2").totalSaleCount(20L).build(),
				BestProduct.builder().productId(3L).name("상품3").totalSaleCount(30L).build(),
				BestProduct.builder().productId(4L).name("상품4").totalSaleCount(40L).build(),
				BestProduct.builder().productId(5L).name("상품5").totalSaleCount(50L).build()
			);

			given(productRepository.getBestProducts(
				any(LocalDateTime.class),
				any(LocalDateTime.class),
				any(Pageable.class)
			))
				.willReturn(bestProducts);

			// when
			final List<BestProductResponse> result = productApplicationService.getBestProducts();

			// then
			assertThat(result).hasSize(5)
				.extracting("name", "totalSaleCount")
				.containsExactly(
					tuple("상품1", 10L),
					tuple("상품2", 20L),
					tuple("상품3", 30L),
					tuple("상품4", 40L),
					tuple("상품5", 50L)
				);
		}
	}
}
