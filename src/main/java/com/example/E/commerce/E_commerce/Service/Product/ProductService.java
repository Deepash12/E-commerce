package com.example.E.commerce.E_commerce.Service.Product;

import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Entity.Product.SubCategory;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Product.CategoryRepository;
import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
import com.example.E.commerce.E_commerce.Repository.Product.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@RequiredArgsConstructor
@Service
public class ProductService
{
    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;

    public ProductPageResponseDTO<ProductResponseDTO> getAllProducts(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir,
            Integer subCategoryId,
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
                        subCategoryId, minPrice, maxPrice, keyword, pageable
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
                .categoryName(product.getSubCategory().getName())
                .build();
    }





    public Product addProduct(ProductRequestDTO productRequestDTO) {

    SubCategory subCategory = subCategoryRepository.findById(
            productRequestDTO.getSubcategoryId()
    ).orElseThrow(() -> new BadRequestException("SubCategory not found"));

    Product product = new Product();
    product.setName(productRequestDTO.getName());
    product.setDescription(productRequestDTO.getDescription());
    product.setPrice(BigDecimal.valueOf(productRequestDTO.getPrice()));
    product.setStockQuantity(productRequestDTO.getStockQuantity());
    product.setSubCategory(subCategory);

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

        SubCategory subCategory = subCategoryRepository.findById(productRequestDTO.getSubcategoryId())
                .orElseThrow(() -> new BadRequestException("SubCategory not found"));

        existingProduct.setName(productRequestDTO.getName());
        existingProduct.setDescription(productRequestDTO.getDescription());
        existingProduct.setPrice(BigDecimal.valueOf(productRequestDTO.getPrice()));
        existingProduct.setStockQuantity(productRequestDTO.getStockQuantity());
        existingProduct.setSubCategory(subCategory);

        return productRepository.save(existingProduct);

    }
}
