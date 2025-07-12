package com.productservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.dto.response.product.ProductResponse;
import com.productservice.event.producer.inventory.ProductCreatedEvent;
import com.productservice.exception.AppException;
import com.productservice.models.Product;
import com.productservice.models.ProductType;
import com.productservice.repositories.ProductRepository;
import com.productservice.repositories.ProductTypeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

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
                .orElseThrow(() -> new AppException(400, "Không tìm thấy loại sản phẩm!"));

        // Kiểm tra trùng tên sản phẩm
        productRepository.findByProductName(name).ifPresent(p -> {
            throw new AppException(400, "Tên sản phẩm đã tồn tại!");
        });

        try {
            // Convert imageUrls -> JSON string
            String imagesJson = objectMapper.writeValueAsString(imageUrls);

            // Tạo sản phẩm mới
            Product newProduct = new Product();
            newProduct.setProductId(UUID.randomUUID());
            newProduct.setProductName(name);
            newProduct.setProductDescription(description);
            newProduct.setProductPrice(price);
            newProduct.setImageUrls(imagesJson);
            newProduct.setProductType(type);
            newProduct.setCreatedAt(LocalDateTime.now());

            // Lưu vào DB
            productRepository.save(newProduct);

            // Gửi event qua Kafka
            kafkaTemplate.send("productCreated", new ProductCreatedEvent(newProduct.getProductId()));

            return newProduct;

        } catch (JsonProcessingException e) {
            throw new AppException(400, "Lỗi khi xử lý ảnh sản phẩm " + e.getMessage());
        } catch (Exception e) {
            throw new AppException(400, "Lỗi khi tạo sản phẩm" + e.getMessage());
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
                .orElseThrow(() -> new AppException(400, "Không tìm thấy sản phẩm"));
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
        Product existing = getProductById(productId);

        // Tìm ProductType theo typeId
        ProductType type = productTypeRepository.findById(typeId)
                .orElseThrow(() -> new AppException(400, "Không tìm thấy loại sản phẩm"));

        try {
            existing.setProductType(type);
            existing.setProductName(name);
            existing.setProductDescription(description);
            existing.setProductPrice(price);
            existing.setActive(active);

            return productRepository.save(existing);

        } catch (Exception e) {
            throw new AppException(500, "Lỗi khi cập nhật sản phẩm"+ e.getMessage());
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
                throw new AppException(400, "Không tìm thấy sản phẩm để xóa!");
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
            throw new AppException(500, "Lỗi hệ thống" + e.getMessage());
        }
    }


    @Transactional
    public void deleteSomeImages(UUID productId, List<String> urlsToDelete) throws JsonProcessingException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(400, "Không tìm thấy sản phẩm"));

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

    @Transactional
    public void addImages(UUID productId, List<String> newImageUrls) throws JsonProcessingException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(400, "Không tìm thấy sản phẩm"));

        // Lấy danh sách ảnh hiện có
        List<String> currentUrls = objectMapper.readValue(
                product.getImageUrls(),
                new TypeReference<List<String>>() {}
        );

        // Thêm các ảnh mới vào danh sách
        currentUrls.addAll(newImageUrls);

        // Cập nhật lại json
        String updatedJson = objectMapper.writeValueAsString(currentUrls);
        product.setImageUrls(updatedJson);
        productRepository.save(product);
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
            throw new AppException(500, "Extract publicId error from url: " + url);
        }
    }


}
