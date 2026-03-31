package com.example.E.commerce.E_commerce.Controller.Product;

import com.example.E.commerce.E_commerce.DTO.ApiResponseDTO;
import com.example.E.commerce.E_commerce.Service.Product.CategoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','USER')")
public class CategoryController
{
    private final CategoryService categoryService;
    @GetMapping("/view/all")
    public ApiResponseDTO<?>viewAllCategory(@RequestParam (defaultValue = "0") Integer pageNumber, @RequestParam (defaultValue = "10") Integer pageSize)
    {
        return new ApiResponseDTO<>(201,"Fetch All Categories", LocalDateTime.now(),categoryService.getAllCategory(pageNumber,pageSize));
    }
}
