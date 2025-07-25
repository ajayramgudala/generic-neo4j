package com.example.genericneo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/export")
public class GraphExportController {

    @Autowired
    private Neo4jClient neo4jClient;

    // Export full graph as GraphML
    @GetMapping(value = "/graphml", produces = "application/xml")
    public ResponseEntity<String> exportGraphML() {
        String cypher = "CALL apoc.export.graphml.all(null, {stream:true}) YIELD data RETURN data";
        String graphml = neo4jClient.query(cypher).fetchAs(String.class).one().orElse("");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=graph.graphml")
                .body(graphml);
    }

    // Export full graph as JSON
    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> exportJson() {
        String cypher = "CALL apoc.export.json.all(null, {stream:true}) YIELD data RETURN data";
        String json = neo4jClient.query(cypher).fetchAs(String.class).one().orElse("");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=graph.json")
                .body(json);
    }

    // Export full graph as CSV
    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<String> exportCsv() {
        String cypher = "CALL apoc.export.csv.all(null, {stream:true}) YIELD data RETURN data";
        String csv = neo4jClient.query(cypher).fetchAs(String.class).one().orElse("");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=graph.csv")
                .body(csv);
    }

    // Visualization-friendly endpoint (Cytoscape/D3.js): nodes and edges as JSON
    @GetMapping("/visualization")
    public Map<String, Object> exportForVisualization() {
        String nodeCypher = "MATCH (n:DocumentNode) RETURN id(n) AS id, n.fieldsJson AS fieldsJson";
        String relCypher = "MATCH (n:DocumentNode)-[r]->(m:DocumentNode) RETURN id(r) AS id, id(n) AS source, id(m) AS target, type(r) AS type, r.propertiesJson AS propertiesJson";
        List<Map<String, Object>> nodes = new ArrayList<>(neo4jClient.query(nodeCypher).fetch().all());
        List<Map<String, Object>> edges = new ArrayList<>(neo4jClient.query(relCypher).fetch().all());
        Map<String, Object> result = new HashMap<>();
        result.put("nodes", nodes);
        result.put("edges", edges);
        return result;
    }

    // Export subgraph as GraphML (by node IDs)
    @GetMapping(value = "/subgraph/graphml", produces = "application/xml")
    public ResponseEntity<String> exportSubgraphGraphML(@RequestParam List<Long> nodeIds) {
        String cypher = "MATCH (n) WHERE id(n) IN $ids WITH collect(n) AS nodes CALL apoc.export.graphml.data(nodes,[],null,{stream:true}) YIELD data RETURN data";
        String graphml = neo4jClient.query(cypher).bindAll(Map.of("ids", nodeIds)).fetchAs(String.class).one().orElse("");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subgraph.graphml")
                .body(graphml);
    }

    // Export subgraph as JSON (by node IDs)
    @GetMapping(value = "/subgraph/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> exportSubgraphJson(@RequestParam List<Long> nodeIds) {
        String cypher = "MATCH (n) WHERE id(n) IN $ids WITH collect(n) AS nodes CALL apoc.export.json.data(nodes,[],null,{stream:true}) YIELD data RETURN data";
        String json = neo4jClient.query(cypher).bindAll(Map.of("ids", nodeIds)).fetchAs(String.class).one().orElse("");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subgraph.json")
                .body(json);
    }

    // Export subgraph as CSV (by node IDs)
    @GetMapping(value = "/subgraph/csv", produces = "text/csv")
    public ResponseEntity<String> exportSubgraphCsv(@RequestParam List<Long> nodeIds) {
        String cypher = "MATCH (n) WHERE id(n) IN $ids WITH collect(n) AS nodes CALL apoc.export.csv.data(nodes,[],null,{stream:true}) YIELD data RETURN data";
        String csv = neo4jClient.query(cypher).bindAll(Map.of("ids", nodeIds)).fetchAs(String.class).one().orElse("");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=subgraph.csv")
                .body(csv);
    }
} 