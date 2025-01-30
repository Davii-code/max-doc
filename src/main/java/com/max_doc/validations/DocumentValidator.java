package com.max_doc.validations;

import com.max_doc.Enum.Stage;
import com.max_doc.entities.Document;
import com.max_doc.exceptions.DocumentValidationException;
import com.max_doc.repository.DocumentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentValidator {

    private final DocumentRepository documentRepository;

    public DocumentValidator(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void validateDocumentCreation(Document document) {
        if (document.getTitle() == null || document.getTitle().isEmpty()) {
            throw new DocumentValidationException("O título não pode ser nulo ou vazio.");
        }
        if (document.getDescription() == null || document.getDescription().isEmpty()) {
            throw new DocumentValidationException("A descrição não pode ser nula ou vazia.");
        }
        if (document.getAbbreviation() == null || document.getAbbreviation().isEmpty()) {
            throw new DocumentValidationException("A abreviação não pode ser nula ou vazia.");
        }
        if (document.getVersion() == null || document.getVersion() < 1) {
            throw new DocumentValidationException("A versão deve ser um número inteiro positivo.");
        }
        if (documentRepository.existsByAbbreviationAndVersion(document.getAbbreviation(), document.getVersion())) {
            throw new DocumentValidationException("A combinação de 'Abreviação' e 'Versão' deve ser única.");
        }
    }

    public void validateDocumentUpdate(Document document) {
        if (!document.getStage().equals(Stage.MINUTA)) {
            throw new DocumentValidationException("O título e a descrição só podem ser editados na fase de minuta.");
        }
    }

    public void validateDocumentSubmission(Document document) {
        if (!document.getStage().equals(Stage.MINUTA)) {
            throw new DocumentValidationException("Apenas documentos em minuta podem ser submetidos.");
        }
    }

    public void validateDocumentObsoletion(Document document) {
        if (!document.getStage().equals(Stage.VIGENTE)) {
            throw new DocumentValidationException("Apenas documentos vigentes podem ser obsoletados.");
        }
    }

    public void validateNewVersionCreation(Document existingDocument) {
        if (!existingDocument.getStage().equals(Stage.VIGENTE)) {
            throw new DocumentValidationException("Apenas documentos vigentes podem ter uma nova versão criada.");
        }
    }

    public void validateDelete(Document existingDocument) {
        if (!existingDocument.getStage().equals(Stage.MINUTA)) {
            throw new DocumentValidationException("Apenas documentos em minuta podem ser excluídos.");
        }
    }

    public void validateExistingIds(List<String> documentIds) {
        if (documentIds!=null) {
            throw new DocumentValidationException("Sem documentos selecionados.");
        }
    }
}
