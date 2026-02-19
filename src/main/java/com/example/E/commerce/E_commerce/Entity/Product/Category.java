package com.example.E.commerce.E_commerce.Entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long ID;
    private String name;
    private String Description;
}
