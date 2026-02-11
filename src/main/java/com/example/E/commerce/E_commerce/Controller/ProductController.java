package com.example.E.commerce.E_commerce.Controller;
//
//import com.example.E.commerce.E_commerce.DTO.Product.ProductRequestDTO;
//import com.example.E.commerce.E_commerce.Entity.Product;
//import com.example.E.commerce.E_commerce.Service.ProductService;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//@EnableMethodSecurity
//@Configuration
//@RequestMapping("/api")
//@RestController
//public class ProductController
//{
//    private final ProductService productService;
//
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }

import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Product;
import com.example.E.commerce.E_commerce.Service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

////
////    public ProductController(ProductService productService)
////    {
////        this.productService = productService;
////    }
//
//    @GetMapping("/products")
//    private List<Product> getAllProducts()
//    {
//
//        return productService.getAllProducts();
//    }
//
//@GetMapping("/products/{id}")
//    public ResponseEntity<Product> getProductById(@PathVariable Long id)
//    {
//        return ResponseEntity.ok(productService.getProductById(id));
//    }
//
//
//
//
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/add")
//    public Product addProduct(@RequestBody ProductRequestDTO productRequestDTO)
//    {
//        return productService.addProduct(productRequestDTO);
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("products/{id}")
//    public ResponseEntity<String> DeleteProduct(@PathVariable Long id)
//    {
//        return ResponseEntity.ok(productService.deleteProductById(id));
//    }
//
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("products/{id}")
//    public ResponseEntity<?> UpdateProductById(@PathVariable Long id, @RequestBody ProductRequestDTO productRequestDTO)
//    {
//        try{
//            Product updateProduct = productService.updateProductById(id,productRequestDTO);
//            return ResponseEntity.ok(updateProduct);
//        }
//        catch (RuntimeException ex){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//        }
//
//    }
//
//}


































@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // PUBLIC
    @GetMapping

    public ProductPageResponseDTO<ProductResponseDTO> getAllProducts
    (
            @RequestParam(required = false) int categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5")int pageSize,
            @RequestParam (defaultValue = "id") String sortBy,
            @RequestParam (defaultValue = "asc") String sortDir
    ) {
        return productService.getAllProducts(pageNumber,pageSize,sortBy,sortDir,categoryId,minPrice,maxPrice,keyword);
    }

    // PUBLIC
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }





    // ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Product addProduct(@RequestBody ProductRequestDTO dto) {
        return productService.addProduct(dto);
    }

    // ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDTO dto) {
        return productService.updateProductById(id, dto);
    }

    // ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        return productService.deleteProductById(id);
    }
}
