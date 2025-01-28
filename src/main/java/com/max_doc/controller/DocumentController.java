package com.max_doc.controller;
import com.max_doc.Enum.Stage;
import com.max_doc.entities.Document;
import com.max_doc.repository.DocumentRepository;
import com.max_doc.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentService documentService;

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Document> createDocument(@RequestBody Document document) {
        if (documentRepository.existsByAbbreviationAndVersion(document.getAbbreviation(), document.getVersion())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        documentService.createDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable String id, @RequestBody Document document) {
        Document existingDocument = documentRepository.findById(id).orElse(null);
        if (existingDocument == null) {
            return ResponseEntity.notFound().build();
        }
        if (!existingDocument.getStage().equals(Stage.MINUTA)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        documentService.updateDocument(document);
        return ResponseEntity.ok(document);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Document> submitDocument(@PathVariable String id) {
        Document document = documentRepository.findById(id).orElse(null);
        if (document == null || !document.getStage().equals(Stage.MINUTA)) {
            return ResponseEntity.badRequest().build();
        }
        documentService.submitDocument(id);
        return ResponseEntity.ok(document);
    }

    @PostMapping("/{id}/obsolete")
    public ResponseEntity<Document> obsoleteDocument(@PathVariable String id) {
        Document document = documentRepository.findById(id).orElse(null);
        if (document == null || !document.getStage().equals(Stage.VIGENTE)) {
            return ResponseEntity.badRequest().build();
        }
        documentService.obsoleteDocument(id);
        return ResponseEntity.ok(document);
    }

    @PostMapping("/{id}/newVersion")
    public ResponseEntity<Document> createNewVersion(@PathVariable String id) {
        Document document = documentRepository.findById(id).orElse(null);
        if (document == null || !document.getStage().equals(Stage.VIGENTE)) {
            return ResponseEntity.badRequest().build();
        }
        Document newVersion = documentService.createNewVersion(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(newVersion);
    }
}
