package com.productservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.productservice.exception.AppException;
import com.productservice.exception.ErrorCode;
import com.productservice.services.ImageService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 255, unique = true)
    private String productName;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "product_price", precision = 15, scale = 2)
    private BigDecimal productPrice;

    @Column(name = "active", nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    @Column(name = "image_urls")
    private List<String> imageUrls = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> productAttributes;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "type_id")
    private ProductType productType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public void createProduct(
            UUID productId,
            String name,
            String description,
            BigDecimal price,
            Map<String, Object> productAttributes,
            List<String> imageUrls,
            ProductType productType
    ){
        this.productId = productId;
        this.productName = name;
        this.productDescription = description;
        this.productPrice = price;
        this.active = true;
        this.imageUrls = imageUrls;
        this.productAttributes = productAttributes;
        this.productType = productType;
        this.createdAt = LocalDateTime.now();
    }

    public void updateProduct(
            ProductType productType,
            String name,
            String description,
            BigDecimal price,
            boolean active){
        this.productType = productType;
        this.productName = name;
        this.productDescription = description;
        this.productPrice = price;
        this.active = active;
    }

    public void addImages(List<String> newUrls) {
        try {
            this.imageUrls.addAll(newUrls);

        } catch (Exception e) {
            throw new AppException(ErrorCode.PRODUCT_IMAGE_PROCESSING_ERROR);
        }
    }

    // Trong class Product.java
    public void deleteImages(List<String> urlsToDelete, ImageService imageService) {
        try {

            for (String url : urlsToDelete) {
                String publicId = extractPublicId(url);
                boolean deleted = imageService.deleteImage(publicId);

                if (deleted) {
                    this.imageUrls.remove(url);
                } else {
                    throw new AppException(ErrorCode.PRODUCT_IMAGE_DELETE_FAILED);
                }
            }

        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AppException(ErrorCode.PRODUCT_IMAGE_PROCESSING_ERROR);
        }
    }


    private String extractPublicId(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();  // /image/upload/v1752139171/products/nokia_0.png
            String[] parts = path.split("/");

            // Tìm vị trí "upload"
            int uploadIdx = Arrays.asList(parts).indexOf("upload");

            // Phần sau "upload/"
            List<String> subParts = new ArrayList<>(Arrays.asList(parts).subList(uploadIdx + 1, parts.length));

            // Nếu phần đầu sau upload là version kiểu "v123456789", thì loại bỏ
            if (!subParts.isEmpty() && subParts.get(0).matches("^v\\d+$")) {
                subParts.remove(0);
            }

            // Ghép lại các phần còn lại: ví dụ ["products", "nokia_0.png"]
            String publicIdWithExt = String.join("/", subParts);

            // Bỏ phần mở rộng (.png, .jpg...)
            return publicIdWithExt.replaceFirst("\\.[^.]+$", "");

        } catch (Exception e) {
            System.out.println("Extract publicId error from url: " + url);
            throw new AppException(ErrorCode.PRODUCT_IMAGE_DELETE_FAILED);
        }
    }

}

