package com.example.E.commerce.E_commerce.Controller.Product;

import com.example.E.commerce.E_commerce.DTO.ApiResponseDTO;
import com.example.E.commerce.E_commerce.Service.Product.SubCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sub-category")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequiredArgsConstructor
public class SubCategoryController
{
    private final SubCategoryService subCategoryService;

    @GetMapping("/view/all")
    public ApiResponseDTO<?> getAllSubCategory(@RequestParam (defaultValue = "0") Integer pageNumber,
                                               @RequestParam (defaultValue = "5") Integer pageSize,
                                               @RequestParam Long categoryId)
    {
        return new ApiResponseDTO<>(201,"Fetch AllSubCategories For Categories",
            LocalDateTime.now(),subCategoryService.fetchSubCategory(pageNumber,pageSize,categoryId));
    }
}
