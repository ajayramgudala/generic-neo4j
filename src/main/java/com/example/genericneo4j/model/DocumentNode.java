package com.example.genericneo4j.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.List;

import java.util.Map;

@Node("DocumentNode")
public class DocumentNode {
    @Id
    @GeneratedValue
    private Long id;

    private String fieldsJson; // Store fields as JSON

    @Transient
    private Map<String, Object> fields; // Not persisted directly

    @Relationship(direction = Relationship.Direction.OUTGOING)
    private Map<String, List<DocumentRelationship>> relationshipsByType;

    public DocumentNode() {}

    public DocumentNode(Map<String, Object> fields) {
        setFields(fields);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldsJson() {
        return fieldsJson;
    }

    public void setFieldsJson(String fieldsJson) {
        this.fieldsJson = fieldsJson;
        this.fields = null; // force re-parse
    }

    public Map<String, Object> getFields() {
        if (fields == null && fieldsJson != null) {
            try {
                fields = new ObjectMapper().readValue(fieldsJson, Map.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
        try {
            this.fieldsJson = new ObjectMapper().writeValueAsString(fields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, List<DocumentRelationship>> getRelationshipsByType() {
        return relationshipsByType;
    }

    public void setRelationshipsByType(Map<String, List<DocumentRelationship>> relationshipsByType) {
        this.relationshipsByType = relationshipsByType;
    }

    // Helper to get all relationships regardless of type
    public List<DocumentRelationship> getAllRelationships() {
        if (relationshipsByType == null) return List.of();
        return relationshipsByType.values().stream().flatMap(List::stream).toList();
    }
} 