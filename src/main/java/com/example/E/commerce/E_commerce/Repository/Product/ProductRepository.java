package com.example.E.commerce.E_commerce.Repository.Product;

import com.example.E.commerce.E_commerce.Entity.Product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product,Long>
{

    @Query("""
SELECT p FROM Product p
WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
AND (:minPrice IS NULL OR p.price >= :minPrice)
AND (:maxPrice IS NULL OR p.price <= :maxPrice)
AND (:keyword IS NULL OR LOWER(p.Name) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
    Page<Product> findWithFilter(Integer categoryId, Double minPrice, Double maxPrice, String keyword, Pageable pageable);

//    List<Product> findByActiveTrue();


}
