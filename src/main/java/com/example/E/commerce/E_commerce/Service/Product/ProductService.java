package com.example.E.commerce.E_commerce.Service.Product;

import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Category;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Product.CategoryRepository;
import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService
{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductPageResponseDTO<ProductResponseDTO> getAllProducts(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            Integer categoryId,
            Double minPrice,
            Double maxPrice,
            String keyword
    ) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Only ONE query should be used
        Page<Product> productPage =
                productRepository.findWithFilter(
                        categoryId, minPrice, maxPrice, keyword, pageable
                );

        List<ProductResponseDTO> dtoList =
                productPage.getContent()
                        .stream()
                        .map(this::convertToDTO)
                        .toList();

        ProductPageResponseDTO<ProductResponseDTO> response =
                new ProductPageResponseDTO<>();

        response.setContent(dtoList);
        response.setCurrentPage(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalPages(productPage.getTotalPages());
        response.setTotalElements(productPage.getTotalElements());
        response.setLast(productPage.isLast());

        return response;
    }





    private ProductResponseDTO convertToDTO(Product product)
    {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryName(product.getCategory().getName())
                .build();
    }





    public Product addProduct(ProductRequestDTO productRequestDTO) {

    Category category = categoryRepository.findById(
            productRequestDTO.getCategoryId()
    ).orElseThrow(() -> new BadRequestException("Category not found"));

    Product product = new Product();
    product.setName(productRequestDTO.getName());
    product.setDescription(productRequestDTO.getDescription());
    product.setPrice(productRequestDTO.getPrice());
    product.setStockQuantity(productRequestDTO.getStockQuantity());
    product.setCategory(category);

    return productRepository.save(product);
}


    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));
    }


    public String deleteProductById(Long id)
    {
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            productRepository.deleteById(id);
            return "Product Deleted Successfully";
        } else {
            return "Product does not exist";
        }
    }

    public Product updateProductById(Long id, ProductRequestDTO productRequestDTO)
    {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product does not exist!!!"));

        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Category not found"));

        existingProduct.setName(productRequestDTO.getName());
        existingProduct.setDescription(productRequestDTO.getDescription());
        existingProduct.setPrice(productRequestDTO.getPrice());
        existingProduct.setStockQuantity(productRequestDTO.getStockQuantity());
        existingProduct.setCategory(category);

        return productRepository.save(existingProduct);

    }
}
