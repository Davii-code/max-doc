package com.max_doc.service;

import com.max_doc.entities.Document;
import java.util.List;

public interface DocumentService {
    Document createDocument(Document document);
    Document updateDocument(Document document);
    void submitDocument(String id);
    void obsoleteDocument(String id);
    Document createNewVersion(Document existingDocument);
    List<Document> getAllDocuments();
}
