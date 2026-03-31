package com.example.E.commerce.E_commerce.Repository.Product;

import com.example.E.commerce.E_commerce.Entity.Product.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubCategoryRepository extends JpaRepository<SubCategory,Long>
{
    @Query("SELECT s FROM SubCategory s WHERE s.category.id = :categoryId")
    Page<SubCategory> findAllByCategoryId(
            Pageable pageable,
            @Param("categoryId") Long categoryId
    );
}
