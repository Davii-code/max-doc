package com.max_doc.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    void importDocuments(MultipartFile file);
}
