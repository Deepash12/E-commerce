package com.example.E.commerce.E_commerce.Exception;

import lombok.*;

import java.time.LocalDateTime;
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO
{

    private int status;
    private String message;
    private LocalDateTime timestamp;
}
