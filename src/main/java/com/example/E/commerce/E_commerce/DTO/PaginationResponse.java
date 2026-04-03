package com.example.E.commerce.E_commerce.DTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.domain.Page;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationResponse<T>
{
    private List<T> data;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public PaginationResponse(List<T> data, Page<?> page) {
        this.data = data;
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalItems = page.getTotalElements();
    }

}
