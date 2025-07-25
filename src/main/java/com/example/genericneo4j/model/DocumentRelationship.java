package com.example.genericneo4j.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipId;

import java.util.Map;

@RelationshipProperties
public class DocumentRelationship {
    @RelationshipId
    private Long id;

    @TargetNode
    private DocumentNode target;

    private Map<String, Object> properties;

    public DocumentRelationship() {}

    public DocumentRelationship(DocumentNode target, Map<String, Object> properties) {
        this.target = target;
        this.properties = properties;
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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
} 