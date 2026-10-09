package org.example.aispingboot.service;

import org.example.aispingboot.common.ResultCode;
import org.example.aispingboot.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Value("${file.upload-path:D:/project3/uploads}")
    private String uploadPath;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(getUploadDir());
    }

    public Map<String, String> saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_CONTENT_INVALID.getMsg());
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ResultCode.FILE_SIZE_EXCEEDED.getMsg());
        }
        String extension = resolveExtension(file);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(ResultCode.FILE_TYPE_NOT_SUPPORTED.getMsg());
        }
        String filename = UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = getUploadDir().resolve(filename).normalize();
        if (!target.startsWith(getUploadDir())) {
            throw new BusinessException(ResultCode.FILE_NAME_INVALID.getMsg());
        }
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.FILE_SAVE_FAILED.getMsg());
        }
        Map<String, String> result = new HashMap<>();
        result.put("fileName", filename);
        result.put("filePath", "/uploads/" + filename);
        result.put("url", "/uploads/" + filename);
        return result;
    }

    public Path getUploadDir() {
        return Paths.get(uploadPath).toAbsolutePath().normalize();
    }

    private String resolveExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase(Locale.ROOT);
            if (ALLOWED_EXTENSIONS.contains(extension)) {
                return extension;
            }
        }
        String contentType = file.getContentType();
        if (contentType == null) {
            return "";
        }
        if (contentType.contains("png")) {
            return ".png";
        }
        if (contentType.contains("gif")) {
            return ".gif";
        }
        if (contentType.contains("webp")) {
            return ".webp";
        }
        if (contentType.contains("jpeg") || contentType.contains("jpg")) {
            return ".jpg";
        }
        return "";
    }
}
