package com.example.genericneo4j.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

import java.util.Map;

@Node("DocumentNode")
public class DocumentNode {
    @Id
    @GeneratedValue
    private Long id;

    private Map<String, Object> fields;

    public DocumentNode() {}

    public DocumentNode(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }
} 