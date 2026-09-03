package com.zest.productapi.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    // Getters and Setters
}