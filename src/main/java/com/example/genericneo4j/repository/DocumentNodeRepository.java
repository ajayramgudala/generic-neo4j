package com.example.genericneo4j.repository;

import com.example.genericneo4j.model.DocumentNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentNodeRepository extends Neo4jRepository<DocumentNode, Long> {
} 