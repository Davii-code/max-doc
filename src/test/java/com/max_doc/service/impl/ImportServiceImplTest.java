package com.max_doc.service.impl;

import org.junit.jupiter.api.Test;
import com.max_doc.entities.Import;
import com.max_doc.exceptions.DocumentValidationException;
import com.max_doc.repository.DocumentRepository;
import com.max_doc.repository.ImportRepository;
import com.max_doc.service.DocumentService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentService documentService;

    @Mock
    private ImportRepository importRepository;

    @InjectMocks
    private ImportServiceImpl importService;

    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Título", "Descrição", "Versão", "Sigla"));

        csvPrinter.printRecord("Documento Teste", "Descrição Teste", "1", "DOC1");
        csvPrinter.flush();

        mockFile = new MockMultipartFile("file", "test.csv", "text/csv", outputStream.toByteArray());
    }

    @Test
    void importDocuments_SuccessfulImport() {
        when(importRepository.save(any(Import.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(documentRepository.existsByAbbreviationAndVersion(any(), any())).thenReturn(false);

        assertDoesNotThrow(() -> importService.importDocuments(mockFile));

        verify(importRepository, times(2)).save(any(Import.class)); // Verifica status atualizado
        verify(documentService, times(1)).createDocument(any());
    }

    @Test
    void importDocuments_DuplicateEntry_ThrowsException() {
        when(importRepository.save(any(Import.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(documentRepository.existsByAbbreviationAndVersion(any(), any())).thenReturn(true);

        assertThrows(DocumentValidationException.class, () -> importService.importDocuments(mockFile));
        verify(importRepository, times(2)).save(any(Import.class)); // Verifica que status foi atualizado para erro
    }


}

