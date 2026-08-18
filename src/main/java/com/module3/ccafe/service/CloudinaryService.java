package com.module3.ccafe.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn hình ảnh");
        }

        try {

            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "smart-cafe/products"
                    )
            );

            return result.get("secure_url").toString();

        } catch (IOException e) {

            throw new RuntimeException(
                    "Upload hình ảnh lên Cloudinary thất bại",
                    e
            );
        }
    }
}
