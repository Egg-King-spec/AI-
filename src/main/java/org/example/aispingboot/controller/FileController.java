package org.example.aispingboot.controller;

import lombok.RequiredArgsConstructor;
import org.example.aispingboot.common.Result;
import org.example.aispingboot.service.FileStorageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('2')")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file,
                                              @RequestParam(required = false) String businessType,
                                              @RequestParam(required = false) String businessId,
                                              @RequestParam(required = false) String businessField) {
        return Result.ok(fileStorageService.saveImage(file));
    }
}
