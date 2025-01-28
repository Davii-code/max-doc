package com.max_doc.controller;

import com.max_doc.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    @PostMapping
    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            importService.importDocuments(file);
            return ResponseEntity.ok("File uploaded and import process started in background.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty file is not allowed.");
        }
    }
}
