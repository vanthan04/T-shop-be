package com.productservice.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {

    private final Cloudinary cloudinary;

    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String upload(MultipartFile file, String productName, int index) {
        try {
            String cleanName = productName.replaceAll("\\s+", "_").toLowerCase();
            String publicId = "products/" + cleanName + "_" + index;

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "public_id", publicId,
                    "resource_type", "auto",
                    "overwrite", true // nếu trùng sẽ ghi đè
            ));

            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }

    public boolean deleteImage(String publicId) {
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String status = (String) result.get("result");
            return "ok".equals(status);
        } catch (IOException e) {
            throw new RuntimeException("Xoá ảnh thất bại: " + publicId, e);
        }
    }



}
