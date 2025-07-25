package com.example.genericneo4j.repository;

import com.example.genericneo4j.model.DocumentRelationship;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRelationshipRepository extends Neo4jRepository<DocumentRelationship, Long> {
} 