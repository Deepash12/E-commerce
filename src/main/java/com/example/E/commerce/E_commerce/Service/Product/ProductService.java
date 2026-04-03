package com.example.E.commerce.E_commerce.Service.Product;

import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductRequestDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Authorization.User;
import com.example.E.commerce.E_commerce.Entity.Product.Category;
import com.example.E.commerce.E_commerce.Entity.Product.Product;
import com.example.E.commerce.E_commerce.Entity.Product.SubCategory;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Product.CategoryRepository;
import com.example.E.commerce.E_commerce.Repository.Product.ProductRepository;
import com.example.E.commerce.E_commerce.Repository.Product.SubCategoryRepository;
import com.example.E.commerce.E_commerce.Repository.User.UserRepository;
import com.example.E.commerce.E_commerce.Repository.Wishlist.WishlistRepository;
import com.example.E.commerce.E_commerce.Service.File.FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService
{
    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final FileService fileService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

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

        Boolean flag = true;

        Page<Product> productPage =
                productRepository.findWithFilter(
                        subCategoryId, minPrice, maxPrice, keyword, flag, pageable
                );

        String username = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            username = auth.getName();
        }

        Set<Object> wishlistProductIds;

        if (username != null) {

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new BadRequestException("User Not Found!!!"));

            wishlistProductIds = wishlistRepository.findByUser(user)
                    .stream()
                    .map(w->w.getProduct().getId())
                    .collect(Collectors.toSet());

        } else {
            wishlistProductIds = new HashSet<>();
        }

        List<ProductResponseDTO> dtoList =
                productPage.getContent()
                        .stream()
                        .map(product -> {
                            ProductResponseDTO dto = convertToDTO(product);
                             dto.setIsWishlist(wishlistProductIds.contains(product.getId()));
                            return dto;
                        })
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
                .categoryName(product.getCategory())
                .discountPrice(product.getDiscountPrice())
                .stockQuantity(product.getStockQuantity())
                .subCategoryName(product.getSubCategory())
                .productImageUrl(product.getProductImageUrl())
                .isActive(product.isActive())
                .build();
    }

    public Product addProduct(ProductRequestDTO productRequestDTO, MultipartFile image) throws IOException {

    SubCategory subCategory = subCategoryRepository.findById(
            productRequestDTO.getSubcategoryId()
    ).orElseThrow(() -> new BadRequestException("SubCategory not found"));

        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(()-> new BadRequestException("Category Not Existed!!!"));

    String productImageUrl = fileService.uploadFile(image);
    Product product = new Product();
    product.setName(productRequestDTO.getName());
    product.setDescription(productRequestDTO.getDescription());
    product.setPrice(BigDecimal.valueOf(productRequestDTO.getPrice()));
    product.setStockQuantity(productRequestDTO.getStockQuantity());
    product.setDiscountPrice(productRequestDTO.getDiscountPrice());
    product.setSubCategory(subCategory);
    product.setCategory(category);
    product.setProductImageUrl(productImageUrl);

    return productRepository.save(product);
}

    public ProductResponseDTO getProductById(Long id) {
        Product product =  productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        return convertToDTO(product);
    }

    @Transactional
    public String deleteProductById(Long id,Boolean flag)
    {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product Not Exist!!!"));

        product.setActive(flag);
        productRepository.save(product);

        return "Product Deleted Successfully";
    }

    public Product updateProductById(Long id, ProductRequestDTO productRequestDTO,MultipartFile image)
            throws IOException
    {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product does not exist!!!"));

        Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
                .orElseThrow(()-> new BadRequestException("Category Not Exist!!!"));

        SubCategory subCategory = subCategoryRepository.findById(productRequestDTO.getSubcategoryId())
                .orElseThrow(() -> new BadRequestException("SubCategory not found"));

        String productImageUrl = fileService.uploadFile(image);
        existingProduct.setName(productRequestDTO.getName());
        existingProduct.setDescription(productRequestDTO.getDescription());
        existingProduct.setPrice(BigDecimal.valueOf(productRequestDTO.getPrice()));
        existingProduct.setStockQuantity(productRequestDTO.getStockQuantity());
        existingProduct.setDiscountPrice(productRequestDTO.getDiscountPrice());
        existingProduct.setCategory(category);
        existingProduct.setSubCategory(subCategory);
        existingProduct.setProductImageUrl(productImageUrl);

        return productRepository.save(existingProduct);

    }

    public ProductPageResponseDTO<ProductResponseDTO> viewAllProduct
            (Integer pageNumber, Integer pageSize, String sortBy, String sortDir, Integer subCategoryId,
             Double minPrice, Double maxPrice, String keyword)
    {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Boolean flag = true;
        Page<Product> productPage =
                productRepository.findWithFilters(
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
}
