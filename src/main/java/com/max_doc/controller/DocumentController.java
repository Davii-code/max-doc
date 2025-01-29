package com.max_doc.controller;
import com.max_doc.entities.Document;
import com.max_doc.repository.DocumentRepository;
import com.max_doc.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.version}/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        Document createdDocument = documentService.createDocument(document);
        return ResponseEntity.status(201).body(createdDocument);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable String id, @RequestBody Document document) {
        Document updatedDocument = documentService.updateDocument(document);
        return ResponseEntity.ok(updatedDocument);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Document> submitDocument(@PathVariable String id) {
        documentService.submitDocument(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/obsolete")
    public ResponseEntity<Document> obsoleteDocument(@PathVariable String id) {
        documentService.obsoleteDocument(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/newVersion")
    public ResponseEntity<Document> createNewVersion(@PathVariable String id) {
        Document document = documentRepository.findById(id).orElse(null);
        Document newVersion = documentService.createNewVersion(document);
        return ResponseEntity.status(201).body(newVersion);
    }
}
