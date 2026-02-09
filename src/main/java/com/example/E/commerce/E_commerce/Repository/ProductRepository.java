package com.example.E.commerce.E_commerce.Repository;

import com.example.E.commerce.E_commerce.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long>
{

//    List<Product> findByActiveTrue();
}
