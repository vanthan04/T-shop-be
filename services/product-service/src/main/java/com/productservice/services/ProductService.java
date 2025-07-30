package com.productservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.dto.response.product.ProductResponse;
import com.productservice.event.producer.inventory.ProductCreatedEvent;
import com.productservice.exception.AppException;
import com.productservice.exception.ErrorCode;
import com.productservice.models.Product;
import com.productservice.models.ProductType;
import com.productservice.repositories.ProductRepository;
import com.productservice.repositories.ProductTypeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ObjectMapper objectMapper;
    private final ImageService imageService;
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

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
        // Kiểm tra loại sản phẩm tồn tại
        ProductType type = productTypeRepository.findById(typeId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));

        // Kiểm tra trùng tên sản phẩm
        productRepository.findByProductName(name).ifPresent(p -> {
            throw new AppException(ErrorCode.PRODUCT_NAME_ALREADY_EXISTS);
        });

        try {
            // Convert imageUrls -> JSON string
            String imagesJson = objectMapper.writeValueAsString(imageUrls);

            // Tạo sản phẩm mới
            Product newProduct = new Product();
            newProduct.createProduct(UUID.randomUUID(), name, description, price, imagesJson, type);
            // Lưu vào DB
            productRepository.save(newProduct);

            // Gửi event qua Kafka
            kafkaTemplate.send("productCreated", new ProductCreatedEvent(newProduct.getProductId()));

            return newProduct;

        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.PRODUCT_IMAGE_PROCESSING_ERROR);
        } catch (Exception e) {
            System.out.println("Lỗi khi tạo sản phẩm" + e.getMessage());
            throw new AppException(ErrorCode.PRODUCT_CREATION_FAILED);
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
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
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
            boolean active
    ) {
        Product product = getProductById(productId);

        // Tìm ProductType theo typeId
        ProductType type = productTypeRepository.findById(typeId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_TYPE_NOT_FOUND));
        try {
            product.updateProduct(type, name, description, price, active);
            return productRepository.save(product);
        } catch (Exception e) {
            System.out.println("Lỗi khi cập nhật sản phẩm"+ e.getMessage());
            throw new AppException(ErrorCode.PRODUCT_UPDATE_FAILED);
        }
    }



    /**
     * Delete product
     */
    @Transactional
    public void deleteProduct(UUID productId) {
        try {
            Optional<Product> existed = productRepository.findById(productId);
            if (!existed.isPresent()){
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            Product delProduct = existed.get();
            // Lấy danh sách ảnh hiện có
            List<String> currentUrls = objectMapper.readValue(
                    delProduct.getImageUrls(),
                    new TypeReference<List<String>>() {}
            );

            deleteSomeImages(delProduct.getProductId(), currentUrls);

            productRepository.delete(delProduct);

        } catch (Exception e){
            System.out.println("Lỗi hệ thống" + e.getMessage());
            throw new AppException(ErrorCode.PRODUCT_INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional
    public void deleteSomeImages(UUID productId, List<String> urlsToDelete) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            // Gọi domain xử lý
            product.deleteImages(urlsToDelete, imageService, objectMapper);

            // Lưu lại
            productRepository.save(product);

        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AppException(ErrorCode.PRODUCT_IMAGE_PROCESSING_ERROR);
        }
    }


    @Transactional
    public void addImages(UUID productId, MultipartFile[] files) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            List<String> newUrls = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                String url = imageService.upload(files[i], product.getProductName(), i);
                newUrls.add(url);
            }

            product.addImages(newUrls, objectMapper);
            productRepository.save(product);

        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AppException(ErrorCode.PRODUCT_IMAGE_PROCESSING_ERROR);
        }
    }



}
