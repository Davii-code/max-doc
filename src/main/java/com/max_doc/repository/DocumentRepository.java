package com.max_doc.repository;


import com.max_doc.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {
    boolean existsByAbbreviationAndVersion(String abbreviation, Integer version);
}
