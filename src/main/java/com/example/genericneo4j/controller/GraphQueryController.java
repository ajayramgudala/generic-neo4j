package com.example.genericneo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/query")
public class GraphQueryController {

    @Autowired
    private Neo4jClient neo4jClient;

    // Pattern matching: find all Invoice nodes connected to PO nodes
    @GetMapping("/pattern/invoice-to-po")
    public List<Map<String, Object>> findInvoiceToPO() {
        String cypher = "MATCH (inv:DocumentNode)-[r]->(po:DocumentNode) " +
                "WHERE inv.fieldsJson CONTAINS 'invoice' AND po.fieldsJson CONTAINS 'PO' " +
                "RETURN id(inv) AS invoiceId, id(po) AS poId, id(r) AS relId, type(r) AS relType";
        return new ArrayList<>(neo4jClient.query(cypher).fetch().all());
    }

    // All paths between two nodes (optionally limit length)
    @GetMapping("/all-paths")
    public List<List<Long>> allPaths(@RequestParam Long fromId, @RequestParam Long toId, @RequestParam(defaultValue = "5") int maxLength) {
        String cypher = "MATCH (start:DocumentNode), (end:DocumentNode) " +
                "WHERE id(start) = $fromId AND id(end) = $toId " +
                "MATCH path = (start)-[*.." + maxLength + "]-(end) " +
                "RETURN [n IN nodes(path) | id(n)] AS nodeIds";
        return neo4jClient.query(cypher)
                .bindAll(Map.of("fromId", fromId, "toId", toId))
                .fetch().all().stream()
                .map(m -> (List<Long>) m.get("nodeIds"))
                .toList();
    }

    // Attribute-based search: find nodes by field value (e.g., all invoices over $1000)
    @GetMapping("/nodes/by-field")
    public List<Map<String, Object>> findNodesByField(@RequestParam String field, @RequestParam String value) {
        String cypher = "MATCH (n:DocumentNode) WHERE n.fieldsJson CONTAINS $value RETURN id(n) AS id, n.fieldsJson AS fieldsJson";
        return new ArrayList<>(neo4jClient.query(cypher).bindAll(Map.of("value", value)).fetch().all());
    }

    // Attribute-based search: find relationships by property value
    @GetMapping("/relationships/by-property")
    public List<Map<String, Object>> findRelationshipsByProperty(@RequestParam String property, @RequestParam String value) {
        String cypher = "MATCH (n:DocumentNode)-[r]->(m:DocumentNode) WHERE r.propertiesJson CONTAINS $value RETURN id(r) AS id, type(r) AS type, r.propertiesJson AS propertiesJson";
        return new ArrayList<>(neo4jClient.query(cypher).bindAll(Map.of("value", value)).fetch().all());
    }
} 