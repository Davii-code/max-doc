package com.max_doc.service.impl;
import com.max_doc.Enum.Stage;
import com.max_doc.entities.Document;
import com.max_doc.repository.DocumentRepository;
import com.max_doc.service.DocumentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional
    public Document createDocument(Document document) {
        if (documentRepository.existsByAbbreviationAndVersion(document.getAbbreviation(), document.getVersion())) {
            throw new IllegalArgumentException("The combination of 'Abbreviation' and 'Version' must be unique.");
        }

        String id = generateDocumentId(document.getAbbreviation(), document.getVersion());
        document.setId(id);

        document.setStage(Stage.MINUTA);
        document.setCreationDate(LocalDateTime.now());
        document.setUpdateDate(LocalDateTime.now());

        return documentRepository.save(document);
    }


    private String generateDocumentId(String abbreviation, Integer version) {
        return abbreviation + "-" + version;
    }

    @Override
    public Document updateDocument(Document document) {
        // Regra: O título e a descrição só podem ser editados na fase minuta.
        if (!document.getStage().equals(Stage.MINUTA)) {
            throw new IllegalArgumentException("Title and description can only be edited in the Draft stage.");
        }

        Optional<Document> existingDocumentOptional = documentRepository.findById(document.getId());
        if (existingDocumentOptional.isPresent()) {
            Document existingDocument = existingDocumentOptional.get();
            existingDocument.setTitle(document.getTitle());
            existingDocument.setDescription(document.getDescription());
            return documentRepository.save(existingDocument);
        } else {
            throw new IllegalArgumentException("Document not found.");
        }
    }

    @Override
    public void submitDocument(String id) {
        Optional<Document> documentOptional = documentRepository.findById(id);
        if (documentOptional.isPresent()) {
            Document document = documentOptional.get();
            // Regra: Um documento minuta pode ser submetido e vai para a fase Vigente.
            if (document.getStage().equals(Stage.MINUTA)) {
                document.setStage(Stage.VIGENTE);
                documentRepository.save(document);
            } else {
                throw new IllegalArgumentException("Only Draft documents can be submitted.");
            }
        } else {
            throw new IllegalArgumentException("Document not found.");
        }
    }

    @Override
    public void obsoleteDocument(String id) {
        Optional<Document> documentOptional = documentRepository.findById(id);
        if (documentOptional.isPresent()) {
            Document document = documentOptional.get();
            // Regra: Um documento vigente pode ser obsoletado e vai para a fase Obsoleto.
            if (document.getStage().equals(Stage.VIGENTE)) {
                document.setStage(Stage.OBSOLETO);
                documentRepository.save(document);
            } else {
                throw new IllegalArgumentException("Only Vigent documents can be obsoleted.");
            }
        } else {
            throw new IllegalArgumentException("Document not found.");
        }
    }

    @Override
    public Document createNewVersion(Document existingDocument) {
        // Regra: Pode ser gerada uma nova versão de um documento vigente.
        if (existingDocument.getStage().equals(Stage.VIGENTE)) {
            Document newVersion = new Document();
            newVersion.setTitle(existingDocument.getTitle());
            newVersion.setDescription(existingDocument.getDescription());
            newVersion.setAbbreviation(existingDocument.getAbbreviation());
            newVersion.setVersion(existingDocument.getVersion() + 1);
            newVersion.setId(generateDocumentId(existingDocument.getAbbreviation(), existingDocument.getVersion()+1));
            newVersion.setStage(Stage.MINUTA);
            obsoleteDocument(existingDocument.getId());
            return documentRepository.save(newVersion);
        } else {
            throw new IllegalArgumentException("Only Vigent documents can have a new version created.");
        }
    }

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
}
