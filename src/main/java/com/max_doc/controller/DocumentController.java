package com.max_doc.controller;
import com.max_doc.Enum.Stage;
import com.max_doc.entities.Document;
import com.max_doc.exceptions.DocumentValidationException;
import com.max_doc.repository.DocumentRepository;
import com.max_doc.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${api.version}/documents")
@CrossOrigin()
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        documentService.deleteDocument(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportDocuments(@RequestBody List<String> documentIds) {
        try {
            byte[] csvBytes = documentService.exportSelectedDocumentsToCSV(documentIds);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documents.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvBytes);
        } catch (DocumentValidationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating CSV: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Document>> filterDocuments(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String abbreviation,
            @RequestParam(required = false) Stage stage) {
            return ResponseEntity.ok(documentService.filterDocuments(title, description, abbreviation, stage));
    }
}
