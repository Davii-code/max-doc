package com.max_doc.service.impl;

import com.max_doc.Enum.Stage;
import com.max_doc.entities.Document;
import com.max_doc.exceptions.DocumentValidationException;
import com.max_doc.repository.DocumentRepository;
import com.max_doc.validations.DocumentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentValidator documentValidator;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private Document document;

    @BeforeEach
    void setUp() {
        document = new Document();
        document.setId("DOC-1");
        document.setTitle("Test Document");
        document.setDescription("Description");
        document.setAbbreviation("DOC");
        document.setVersion(1);
        document.setStage(Stage.MINUTA);
        document.setCreationDate(LocalDateTime.now());
        document.setUpdateDate(LocalDateTime.now());
    }

    @Test
    void createDocument() {
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        Document createdDocument = documentService.createDocument(document);

        assertNotNull(createdDocument);
        assertEquals("DOC-1", createdDocument.getId());
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void updateDocument() {
        when(documentRepository.findById("DOC-1")).thenReturn(Optional.of(document));
        when(documentRepository.save(any(Document.class))).thenReturn(document);

        document.setTitle("Updated Title");
        Document updatedDocument = documentService.updateDocument(document);

        assertNotNull(updatedDocument);
        assertEquals("Updated Title", updatedDocument.getTitle());
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void submitDocument() {
        when(documentRepository.findById("DOC-1")).thenReturn(Optional.of(document));

        documentService.submitDocument("DOC-1");

        assertEquals(Stage.VIGENTE, document.getStage());
        verify(documentRepository, times(1)).save(document);
    }

    @Test
    void obsoleteDocument() {
        when(documentRepository.findById("DOC-1")).thenReturn(Optional.of(document));

        documentService.obsoleteDocument("DOC-1");

        assertEquals(Stage.OBSOLETO, document.getStage());
        verify(documentRepository, times(1)).save(document);
    }

    @Test
    void createNewVersion() {
        when(documentRepository.findById(document.getId())).thenReturn(Optional.of(document));

        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Document newVersion = documentService.createNewVersion(document);

        assertNotNull(newVersion);
        assertEquals(document.getTitle(), newVersion.getTitle());
        assertEquals(document.getDescription(), newVersion.getDescription());
        assertEquals(document.getAbbreviation(), newVersion.getAbbreviation());
        assertEquals(document.getVersion() + 1, newVersion.getVersion());
        assertEquals(Stage.MINUTA, newVersion.getStage());
        assertNotNull(newVersion.getCreationDate());
        assertNotNull(newVersion.getUpdateDate());

        verify(documentValidator).validateNewVersionCreation(document);
        verify(documentRepository).findById(document.getId());
        verify(documentRepository).save(newVersion);
    }

    @Test
    void getDocumentById() {
        when(documentRepository.findById("DOC-1")).thenReturn(Optional.of(document));

        Document foundDocument = documentService.getDocumentById("DOC-1");

        assertNotNull(foundDocument);
        assertEquals("DOC-1", foundDocument.getId());
    }

    @Test
    void getDocumentById_NotFound() {
        when(documentRepository.findById("DOC-99")).thenReturn(Optional.empty());

        assertThrows(DocumentValidationException.class, () -> documentService.getDocumentById("DOC-99"));
    }

    @Test
    void deleteDocument() {
        when(documentRepository.findById("DOC-1")).thenReturn(Optional.of(document));
        doNothing().when(documentRepository).deleteById("DOC-1");

        documentService.deleteDocument("DOC-1");

        verify(documentRepository, times(1)).deleteById("DOC-1");
    }

    @Test
    void filterDocuments() {
        when(documentRepository.findAll()).thenReturn(Arrays.asList(document));

        List<Document> result = documentService.filterDocuments("Test Document", "", "", Stage.MINUTA);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Document", result.get(0).getTitle());
    }
}
