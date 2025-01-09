package io.hhplus.ecommerce.product.infra;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import io.hhplus.ecommerce.product.domain.BestProduct;
import io.hhplus.ecommerce.product.domain.Product;

public interface ProductJpaRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	Page<Product> findAll(Specification<Product> spec, Pageable pageable);

	@Query(value = """
		select new io.hhplus.ecommerce.product.domain.BestProduct(
			p.id,
			p.name,
			sum(oi.quantity)
		)
		from Product p
			join OrderItem oi on p.id = oi.productId and oi.deletedAt is null
			join Order o on oi.orderId = o.id and o.deletedAt is null
		where p.deletedAt is null
		  and o.updatedAt between :startDateTime and :endDateTime
		group by p.id
		order by sum(oi.quantity) desc
	""")
	List<BestProduct> getBestProducts(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);
}
