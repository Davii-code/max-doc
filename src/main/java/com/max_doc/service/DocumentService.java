package com.max_doc.service;

import com.max_doc.Enum.Stage;
import com.max_doc.entities.Document;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    Document createDocument(Document document);
    Document updateDocument(Document document);
    void submitDocument(String id);
    void obsoleteDocument(String id);
    Document createNewVersion(Document existingDocument);
    List<Document> getAllDocuments();
    Document getDocumentById(String id);
    void deleteDocument(String id);
    byte[] exportSelectedDocumentsToCSV(List<String> documentIds) ;
    public List<Document> filterDocuments(String title, String description, String abbreviation, Stage stage);
}
