package com.example.E.commerce.E_commerce.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T>
{
        private int status;
        private String message;
        private LocalDateTime timestamp;
        private T data;
}
