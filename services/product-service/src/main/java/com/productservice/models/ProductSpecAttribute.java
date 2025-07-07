package com.productservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_spec_attributes")
@Data
public class ProductSpecAttribute {

    @Id
    @Column(name = "attribute_id")
    private UUID attributeId;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    @JsonBackReference
    private ProductType productType;

    @Column(name = "attribute_name", length = 100, nullable = false)
    private String attributeName;

    @Column(name = "data_type", length = 20, nullable = false)
    private String dataType; // string, number, boolean

}
