package com.example.genericneo4j.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.schema.RelationshipId;

import java.util.Map;

@RelationshipProperties
public class DocumentRelationship {
    @RelationshipId
    private Long id;

    @TargetNode
    private DocumentNode target;

    private String propertiesJson; // Store properties as JSON

    @Transient
    private Map<String, Object> properties; // Not persisted directly

    public DocumentRelationship() {}

    public DocumentRelationship(DocumentNode target, Map<String, Object> properties) {
        this.target = target;
        setProperties(properties);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentNode getTarget() {
        return target;
    }

    public void setTarget(DocumentNode target) {
        this.target = target;
    }

    public String getPropertiesJson() {
        return propertiesJson;
    }

    public void setPropertiesJson(String propertiesJson) {
        this.propertiesJson = propertiesJson;
        this.properties = null; // force re-parse
    }

    public Map<String, Object> getProperties() {
        if (properties == null && propertiesJson != null) {
            try {
                properties = new ObjectMapper().readValue(propertiesJson, Map.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
        try {
            this.propertiesJson = new ObjectMapper().writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
} 