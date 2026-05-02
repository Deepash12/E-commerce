package com.example.E.commerce.E_commerce.Service.Product;

import com.example.E.commerce.E_commerce.DTO.Product.CategoryResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.SubCategoryResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Product.Category;
import com.example.E.commerce.E_commerce.Entity.Product.SubCategory;
import com.example.E.commerce.E_commerce.Exception.BadRequestException;
import com.example.E.commerce.E_commerce.Repository.Product.CategoryRepository;
import com.example.E.commerce.E_commerce.Repository.Product.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryService
{
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    private SubCategoryResponseDTO mapToDTO(SubCategory subCategory)
    {
        return new SubCategoryResponseDTO
                (
                subCategory.getId(), subCategory.getName(), subCategory.getDescription(),
                new CategoryResponseDTO
                        (subCategory.getCategory().getId(),
                        subCategory.getCategory().getName(),subCategory.getCategory().getDescription()
                        )
                );
    }
    public ProductPageResponseDTO<SubCategoryResponseDTO> fetchSubCategory(Integer pageNumber, Integer pageSize, Long categoryId)
    {
        Category category = categoryRepository.findById
                (categoryId).orElseThrow(()-> new BadRequestException("Category Not Existed!!!"));

        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<SubCategory> subCategories = subCategoryRepository.findAllByCategoryId(pageable,categoryId);

        List<SubCategoryResponseDTO> dtoList = subCategories.getContent().stream().map
                (
                        this::mapToDTO
                ).toList();

        ProductPageResponseDTO<SubCategoryResponseDTO> response = new ProductPageResponseDTO<>();
        response.setContent(dtoList);
        response.setCurrentPage(subCategories.getNumber());
        response.setPageSize(subCategories.getSize());
        response.setTotalPages(subCategories.getTotalPages());
        response.setTotalElements(subCategories.getTotalElements());
        response.setLast(subCategories.isLast());
        return response;
    }
}
