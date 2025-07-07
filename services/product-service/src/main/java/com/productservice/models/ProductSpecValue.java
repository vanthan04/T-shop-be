package com.productservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "product_spec_values")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSpecValue {

    @Id
    @Column(name = "value_id")
    private UUID valueId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "attribute_id", nullable = false)
    private ProductSpecAttribute attribute;

    @Column(name = "value", columnDefinition = "text", nullable = false)
    private String value;

}
