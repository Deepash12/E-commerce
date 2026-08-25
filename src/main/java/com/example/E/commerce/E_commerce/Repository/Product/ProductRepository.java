package com.example.E.commerce.E_commerce.Repository.Product;

import com.example.E.commerce.E_commerce.Entity.Product.Product;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long>
{
    @Query("""
SELECT p FROM Product p
WHERE (:subCategoryId IS NULL OR p.subCategory.id = :subCategoryId)
AND (:minPrice IS NULL OR p.price >= :minPrice)
AND (:maxPrice IS NULL OR p.price <= :maxPrice)
AND (
    :keyword IS NULL
    OR LOWER(p.name) LIKE LOWER(CAST(:keyword AS string))
)

AND (:flag IS NULL OR p.isActive = :flag)
""")
//    AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER (:keyword))
//    (CONCAT('%', :keyword, '%')))
    Page<Product> findWithFilter(Integer subCategoryId, Double minPrice, Double maxPrice, String keyword,Boolean flag, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);


//    @Query("""
//SELECT p FROM Product p
//WHERE (:subCategoryId IS NULL OR p.subCategory.id = :subCategoryId)
//AND (:minPrice IS NULL OR p.price >= :minPrice)
//AND (:maxPrice IS NULL OR p.price <= :maxPrice)
//AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(:keyword))
//""")

    @Query("""
SELECT p FROM Product p
WHERE (:subCategoryId IS NULL OR p.subCategory.id = :subCategoryId)
AND (:minPrice IS NULL OR p.price >= :minPrice)
AND (:maxPrice IS NULL OR p.price <= :maxPrice)
AND LOWER(p.name) LIKE CONCAT('%', :keyword, '%')
""")


        //(CONCAT('%', :keyword, '%')))
    Page<Product> findWithFilters(Integer subCategoryId, Double minPrice, Double maxPrice, String keyword, Pageable pageable);

    boolean existsByNameIgnoreCase(String trim);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
