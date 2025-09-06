package com.productservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_types")
public class ProductType {

    @Id
    @Column(name = "type_id")
    private UUID typeId;

    @Column(name = "type_name", length = 100, unique = true, nullable = false)
    private String typeName;

    @OneToMany(mappedBy = "productType", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Product> products;


    public ProductType(UUID typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

}
