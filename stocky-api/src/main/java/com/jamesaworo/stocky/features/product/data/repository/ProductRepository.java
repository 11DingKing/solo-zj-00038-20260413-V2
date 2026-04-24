package com.jamesaworo.stocky.features.product.data.repository;

import com.jamesaworo.stocky.features.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	Page<Product> findAll(Specification<Product> specification, Pageable pageable);

	List<Product> findAll(Specification<Product> specification);

	@Query("SELECT p FROM Product p JOIN FETCH p.basic b LEFT JOIN FETCH b.productCategory " +
			"WHERE b.lowStockPoint IS NOT NULL AND b.quantity <= b.lowStockPoint " +
			"AND b.isActive = true AND p.isActiveStatus = true " +
			"ORDER BY (b.lowStockPoint - b.quantity) DESC")
	List<Product> findLowStockProducts();

	@Query("SELECT COUNT(p) FROM Product p JOIN p.basic b " +
			"WHERE b.lowStockPoint IS NOT NULL AND b.quantity <= b.lowStockPoint " +
			"AND b.isActive = true AND p.isActiveStatus = true")
	Long countLowStockProducts();

	@Query("SELECT p FROM Product p JOIN FETCH p.basic b JOIN FETCH b.productCategory c " +
			"WHERE c.id IN :categoryIds AND b.isActive = true AND p.isActiveStatus = true")
	List<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);

	@Modifying
	@Query("UPDATE ProductBasic b SET b.lowStockPoint = :lowStockPoint WHERE b.productCategory.id IN :categoryIds")
	int updateLowStockPointByCategoryIds(@Param("categoryIds") List<Long> categoryIds, @Param("lowStockPoint") Integer lowStockPoint);
}