package com.productservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.dto.response.product.ProductResponse;
import com.productservice.models.Product;
import com.productservice.models.ProductType;
import com.productservice.repositories.ProductRepository;
import com.productservice.repositories.ProductTypeRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ObjectMapper objectMapper;
    private final ImageService imageService;

    public ProductService(
            ProductRepository productRepository,
            ProductTypeRepository productTypeRepository,
            ObjectMapper objectMapper,
            ImageService imageService) {
        this.productRepository = productRepository;
        this.productTypeRepository = productTypeRepository;
        this.objectMapper = objectMapper;
        this.imageService = imageService;
    }

    /**
     * Create product
     */
    public Product createProduct(
            UUID typeId,
            String name,
            String description,
            BigDecimal price,
            List<String> imageUrls
    ) {
        ProductType type = productTypeRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại sản phẩm!"));

        Optional<Product> isExisted = productRepository.findByProductName(name);
        if (isExisted.isPresent()) {
            throw new RuntimeException("Tên sản phẩm đã tồn tại!");
        }

        try {
            String imagesJson = objectMapper.writeValueAsString(imageUrls);

            Product product = new Product();
            product.setProductId(UUID.randomUUID());
            product.setProductName(name);
            product.setProductDescription(description);
            product.setProductPrice(price);
            product.setImageUrls(imagesJson);
            product.setProductType(type);
            product.setCreatedAt(LocalDateTime.now());

            return productRepository.save(product);

        } catch (Exception e) {
            throw new RuntimeException("Error creating product", e);
        }
    }

    /**
     * Get all products
     */
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(product -> {
            ProductResponse dto = new ProductResponse();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setProductDescription(product.getProductDescription());
            dto.setProductPrice(product.getProductPrice());
            try {
                dto.setImageUrls(objectMapper.readValue(product.getImageUrls(), new TypeReference<List<String>>() {}));
            } catch (Exception e) {
                dto.setImageUrls(List.of());
            }
            dto.setTypeId(product.getProductType() != null ? product.getProductType().getTypeId() : null);
            dto.setTypeName(product.getProductType() != null ? product.getProductType().getTypeName() : null);

            return dto;
        }).toList();
    }


    /**
     * Get product by ID
     */
    public Product getProductById(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    /**
     * Update product
     */
    public Product updateProduct(
            UUID productId,
            UUID typeId,
            String name,
            String description,
            BigDecimal price,
            List<String> imageUrls
    ) {
        Product existing = getProductById(productId);

        // Tìm ProductType theo typeId
        ProductType type = productTypeRepository.findById(typeId)
                .orElseThrow(() -> new NotFoundException("Product type not found"));

        try {
            existing.setProductType(type);
            existing.setProductName(name);
            existing.setProductDescription(description);
            existing.setProductPrice(price);
            existing.setImageUrls(objectMapper.writeValueAsString(imageUrls));

            return productRepository.save(existing);

        } catch (Exception e) {
            throw new RuntimeException("Error updating product", e);
        }
    }



    /**
     * Delete product
     */
    public void deleteProduct(UUID productId) {
        productRepository.deleteById(productId);
    }


    @Transactional
    public void deleteSomeImages(UUID productId, List<String> urlsToDelete) throws JsonProcessingException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy product"));

        // Lấy danh sách ảnh hiện có
        List<String> currentUrls = objectMapper.readValue(
                product.getImageUrls(),
                new TypeReference<List<String>>() {}
        );

        for (String url : urlsToDelete) {
            String publicId = extractPublicId(url);
            boolean deleted = imageService.deleteImage(publicId);
            if (deleted) {
                System.out.println("Deleted: " + publicId);
                currentUrls.remove(url);
            } else {
                System.out.println("Không xoá được: " + publicId);
            }
        }


        // Cập nhật lại imageUrls trong DB
        String updatedJson = objectMapper.writeValueAsString(currentUrls);
        product.setImageUrls(updatedJson);
        productRepository.save(product);
    }


    private String extractPublicId(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();  // /demo/image/upload/v123/products/nokia_1280_0.jpg
            String[] parts = path.split("/");
            // lấy từ "upload/" trở đi
            int uploadIdx = Arrays.asList(parts).indexOf("upload");
            String publicIdWithExt = String.join("/", Arrays.copyOfRange(parts, uploadIdx + 1, parts.length));
            // bỏ đuôi .jpg/.png
            return publicIdWithExt.replaceFirst("\\.[^.]+$", "");
        } catch (Exception e) {
            throw new RuntimeException("Extract publicId error from url: " + url);
        }
    }

}
