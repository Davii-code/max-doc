package com.max_doc.validations;

import com.max_doc.Enum.Stage;
import com.max_doc.entities.Document;
import com.max_doc.exceptions.DocumentValidationException;
import com.max_doc.repository.DocumentRepository;
import org.springframework.stereotype.Component;

@Component
public class DocumentValidator {

    private final DocumentRepository documentRepository;

    public DocumentValidator(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void validateDocumentCreation(Document document) {
        if (document.getTitle() == null || document.getTitle().isEmpty()) {
            throw new DocumentValidationException("Title cannot be null or empty.");
        }
        if (document.getDescription() == null || document.getDescription().isEmpty()) {
            throw new DocumentValidationException("Description cannot be null or empty.");
        }
        if (document.getAbbreviation() == null || document.getAbbreviation().isEmpty()) {
            throw new DocumentValidationException("Abbreviation cannot be null or empty.");
        }
        if (document.getVersion() == null || document.getVersion() < 1) {
            throw new DocumentValidationException("Version must be a positive integer.");
        }
        if (documentRepository.existsByAbbreviationAndVersion(document.getAbbreviation(), document.getVersion())) {
            throw new DocumentValidationException("The combination of 'Abbreviation' and 'Version' must be unique.");
        }
    }

    public void validateDocumentUpdate(Document document) {
        if (!document.getStage().equals(Stage.MINUTA)) {
            throw new DocumentValidationException("Title and description can only be edited in the Draft stage.");
        }
    }

    public void validateDocumentSubmission(Document document) {
        if (!document.getStage().equals(Stage.MINUTA)) {
            throw new DocumentValidationException("Only Draft documents can be submitted.");
        }
    }

    public void validateDocumentObsoletion(Document document) {
        if (!document.getStage().equals(Stage.VIGENTE)) {
            throw new DocumentValidationException("Only Vigent documents can be obsoleted.");
        }
    }

    public void validateNewVersionCreation(Document existingDocument) {
        if (!existingDocument.getStage().equals(Stage.VIGENTE)) {
            throw new DocumentValidationException("Only Vigent documents can have a new version created.");
        }
    }

    public void validateDelete(Document existingDocument) {
        if (!existingDocument.getStage().equals(Stage.MINUTA)) {
            throw new DocumentValidationException("Only Vigent documents can have a new version created.");
        }
    }
}
