package com.max_doc.service.impl;

import com.max_doc.Enum.Stage;
import com.max_doc.entities.Document;
import com.max_doc.repository.DocumentRepository;
import com.max_doc.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

@Service
public class ImportServiceImpl implements ImportService {

    private final DocumentRepository documentRepository;

    @Autowired
    public ImportServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public void importDocuments(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Supõe-se que você terá uma lógica para ler o CSV e criar documentos.
            // Este é um exemplo simplificado.
            String line;
            while ((line = reader.readLine()) != null) {
                // Aqui você deve parsear a linha do CSV para criar um Documento.
                // As regras de negócios devem ser aplicadas aqui, como a verificação de duplicidade.
                Document document = parseDocumentFromCsv(line);
                documentRepository.save(createDocument(document));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading the CSV file.", e);
        }
    }

    private Document parseDocumentFromCsv(String csvLine) {
        // Implemente a lógica para parsear a linha do CSV e criar um Documento.
        // Este método é um placeholder e deve ser substituído pela lógica real.
        Document document = new Document();
        // Suponha que a linha CSV tenha os dados no formato esperado.
        // Aqui você deve atribuir os valores corretos para título, descrição, versão, sigla, etc.
        document.setTitle("Sample Title");
        document.setDescription("Sample Description");
        document.setVersion(1);
        document.setAbbreviation("SIG");
        return document;
    }

    private Document createDocument(Document document) {
        // Verifica a unicidade da combinação de sigla e versão.
        if (documentRepository.existsByAbbreviationAndVersion(document.getAbbreviation(), document.getVersion())) {
            throw new IllegalArgumentException("The combination of 'Abbreviation' and 'Version' must be unique.");
        }
        // Define a fase inicial como Minuta.
        document.setStage(Stage.MINUTA);
        return document;
    }
}
