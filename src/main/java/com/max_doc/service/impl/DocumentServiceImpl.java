package com.max_doc.service.impl;

import com.max_doc.Enum.Stage;
import com.max_doc.entities.Document;
import com.max_doc.exceptions.DocumentValidationException;
import com.max_doc.repository.DocumentRepository;
import com.max_doc.service.DocumentService;
import com.max_doc.validations.DocumentValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentValidator documentValidator;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentValidator documentValidator) {
        this.documentRepository = documentRepository;
        this.documentValidator = documentValidator;
    }

    @Override
    @Transactional
    public Document createDocument(Document document) {
        documentValidator.validateDocumentCreation(document);

        document.setId(generateDocumentId(document.getAbbreviation(), document.getVersion()));
        document.setStage(Stage.MINUTA);
        document.setCreationDate(LocalDateTime.now());
        document.setUpdateDate(LocalDateTime.now());

        return documentRepository.save(document);
    }

    @Override
    public Document updateDocument(Document document) {
        documentValidator.validateDocumentUpdate(document);
        Document existingDocument = getDocumentById(document.getId());
        existingDocument.setTitle(document.getTitle());
        existingDocument.setDescription(document.getDescription());
        existingDocument.setUpdateDate(LocalDateTime.now());

        return documentRepository.save(existingDocument);
    }

    @Override
    public void submitDocument(String id) {
        Document document = getDocumentById(id);
        documentValidator.validateDocumentSubmission(document);
        document.setStage(Stage.VIGENTE);
        document.setUpdateDate(LocalDateTime.now());
        documentRepository.save(document);
    }

    @Override
    public void obsoleteDocument(String id) {
        Document document = getDocumentById(id);
        documentValidator.validateDocumentObsoletion(document);
        document.setStage(Stage.OBSOLETO);
        document.setUpdateDate(LocalDateTime.now());
        documentRepository.save(document);
    }

    @Override
    public Document createNewVersion(Document existingDocument) {
        documentValidator.validateNewVersionCreation(existingDocument);

        Document newVersion = new Document();
        newVersion.setTitle(existingDocument.getTitle());
        newVersion.setDescription(existingDocument.getDescription());
        newVersion.setAbbreviation(existingDocument.getAbbreviation());
        newVersion.setVersion(existingDocument.getVersion() + 1);
        newVersion.setId(generateDocumentId(existingDocument.getAbbreviation(), existingDocument.getVersion() + 1));
        newVersion.setStage(Stage.MINUTA);
        newVersion.setCreationDate(LocalDateTime.now());
        newVersion.setUpdateDate(LocalDateTime.now());

        obsoleteDocument(existingDocument.getId());
        return documentRepository.save(newVersion);
    }

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document getDocumentById(String id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new DocumentValidationException("Document not found."));
    }

    public void deleteDocument(String id) {
       Document document = getDocumentById(id);
        documentValidator.validateDelete(document);
        documentRepository.deleteById(id);
    }

    private String generateDocumentId(String abbreviation, Integer version) {
        return abbreviation + "-" + version;
    }
}
