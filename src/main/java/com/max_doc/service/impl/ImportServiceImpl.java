package com.max_doc.service.impl;

import com.max_doc.Enum.ImportStatus;
import com.max_doc.Enum.Stage;
import com.max_doc.dto.DocumentDTO;
import com.max_doc.entities.Document;
import com.max_doc.entities.Import;
import com.max_doc.exceptions.DocumentValidationException;
import com.max_doc.repository.DocumentRepository;
import com.max_doc.repository.ImportRepository;
import com.max_doc.service.DocumentService;
import com.max_doc.service.ImportService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ImportServiceImpl implements ImportService {

    private final DocumentRepository documentRepository;
    private final DocumentService documentService;
    private final ImportRepository importRepository;

    @Autowired
    public ImportServiceImpl(DocumentRepository documentRepository, DocumentService documentService, ImportRepository importRepository) {
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.importRepository = importRepository;
    }

    @Async
    @Override
    public void importDocuments(MultipartFile file) {
        Import importEntity = new Import();
        importEntity.setFileName(file.getOriginalFilename());
        importEntity.setStatus(ImportStatus.EM_ANDAMENTO);
        importRepository.save(importEntity);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            List<DocumentDTO> documentDTOList = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                DocumentDTO documentDTO = parseDocumentFromCsv(record);
                documentDTOList.add(documentDTO);
            }

            saveDocuments(documentDTOList);

            importEntity.setStatus(ImportStatus.CONCLUIDO);
        } catch (IOException e) {
            importEntity.setStatus(ImportStatus.ERRO);
            importEntity.setErrors("Erro com o CSV: " + e.getMessage());
        } finally {
            importEntity.setEndDate(java.time.LocalDateTime.now());
            importRepository.save(importEntity);
        }
    }

    private DocumentDTO parseDocumentFromCsv(CSVRecord record) {
        String title = record.get("Título");
        String description = record.get("Descrição");
        String versionStr = record.get("Versão");
        String abbreviation = record.get("Sigla");

        if (title == null || title.isEmpty()) {
            throw new DocumentValidationException("O título está ausente no CSV.");
        }
        if (description == null || description.isEmpty()) {
            throw new DocumentValidationException("A descrição está ausente no CSV.");
        }
        if (versionStr == null || versionStr.isEmpty()) {
            throw new DocumentValidationException("A versão está ausente no CSV.");
        }
        if (abbreviation == null || abbreviation.isEmpty()) {
            throw new DocumentValidationException("A sigla está ausente no CSV.");
        }

        int version;
        try {
            version = Integer.parseInt(versionStr);
        } catch (NumberFormatException e) {
            throw new DocumentValidationException("Formato de versão inválido: " + versionStr);
        }

        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setTitulo(title);
        documentDTO.setDescricao(description);
        documentDTO.setVersao(version);
        documentDTO.setSigla(abbreviation);

        return documentDTO;
    }

    private void saveDocuments(List<DocumentDTO> documentDTOList) {
        for (DocumentDTO documentDTO : documentDTOList) {
            if (documentRepository.existsByAbbreviationAndVersion(documentDTO.getSigla(), documentDTO.getVersao())) {
                throw new DocumentValidationException("A combinação de 'Sigla' e 'Versão' deve ser única.");
            }

            Document document = new Document();
            document.setTitle(documentDTO.getTitulo());
            document.setDescription(documentDTO.getDescricao());
            document.setVersion(documentDTO.getVersao());
            document.setAbbreviation(documentDTO.getSigla());
            document.setStage(Stage.MINUTA);

            documentService.createDocument(document);
        }
    }
}
