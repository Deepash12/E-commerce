package com.example.E.commerce.E_commerce.Service.Product;

import com.example.E.commerce.E_commerce.DTO.Product.CategoryResponseDTO;
import com.example.E.commerce.E_commerce.DTO.Product.ProductPageResponseDTO;
import com.example.E.commerce.E_commerce.Entity.Product.Category;
import com.example.E.commerce.E_commerce.Repository.Product.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService
{
    private final CategoryRepository categoryRepository;
    public ProductPageResponseDTO<CategoryResponseDTO> getAllCategory(Integer pageNumber, Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<Category> category = categoryRepository.findAll(pageable);

        List<CategoryResponseDTO> dtoList =
                category.getContent()
                        .stream()
                        .map(this::convertToDTO)
                        .toList();

        ProductPageResponseDTO<CategoryResponseDTO> response = new ProductPageResponseDTO<>();
        response.setContent(dtoList);
        response.setCurrentPage(category.getNumber());
        response.setPageSize(category.getSize());
        response.setTotalPages(category.getTotalPages());
        response.setTotalElements(category.getTotalElements());
        response.setLast(category.isLast());
        return response;
    }

    private CategoryResponseDTO convertToDTO(Category category)
    {
        return new CategoryResponseDTO(category.getID(), category.getName(), category.getDescription());
    }
}
