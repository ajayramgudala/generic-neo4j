package com.example.genericneo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class GraphAdminController {

    @Autowired
    private Neo4jClient neo4jClient;

    // Query profiling endpoint
    @PostMapping("/profile")
    public Map<String, Object> profileQuery(@RequestBody Map<String, String> body) {
        String cypher = body.getOrDefault("cypher", "MATCH (n) RETURN count(n)");
        String profileCypher = "PROFILE " + cypher;
        List<Map<String, Object>> plan = new ArrayList<>(neo4jClient.query(profileCypher).fetch().all());
        Map<String, Object> result = new HashMap<>();
        result.put("plan", plan);
        return result;
    }

    // Graph statistics
    @GetMapping("/stats")
    public Map<String, Object> graphStats() {
        Map<String, Object> stats = new HashMap<>();
        String nodeCountCypher = "MATCH (n:DocumentNode) RETURN count(n) AS nodeCount";
        String relCountCypher = "MATCH ()-[r]->() RETURN count(r) AS relCount";
        String degreeDistCypher = "MATCH (n:DocumentNode) RETURN id(n) AS id, size((n)--()) AS degree";
        stats.put("nodeCount", neo4jClient.query(nodeCountCypher).fetch().one().orElse(Map.of("nodeCount", 0)));
        stats.put("relCount", neo4jClient.query(relCountCypher).fetch().one().orElse(Map.of("relCount", 0)));
        stats.put("degreeDistribution", neo4jClient.query(degreeDistCypher).fetch().all());
        return stats;
    }

    // Bulk import from CSV (nodes only, simple example)
    @PostMapping("/import/csv/nodes")
    public ResponseEntity<String> importNodesCsv(@RequestParam("file") MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                // Assume CSV: field1,field2,...
                String[] fields = line.split(",");
                Map<String, Object> map = new HashMap<>();
                for (int i = 0; i < fields.length; i++) {
                    map.put("field" + (i + 1), fields[i]);
                }
                String cypher = "CREATE (n:DocumentNode {fieldsJson: $fieldsJson})";
                neo4jClient.query(cypher).bindAll(Map.of("fieldsJson", new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(map))).run();
                count++;
            }
            return ResponseEntity.ok("Imported " + count + " nodes.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Batch update nodes (by ID)
    @PostMapping("/batch-update/nodes")
    public ResponseEntity<String> batchUpdateNodes(@RequestBody List<Map<String, Object>> updates) {
        int updated = 0;
        for (Map<String, Object> update : updates) {
            Long id = ((Number) update.get("id")).longValue();
            String fieldsJson = (String) update.get("fieldsJson");
            String cypher = "MATCH (n:DocumentNode) WHERE id(n) = $id SET n.fieldsJson = $fieldsJson";
            neo4jClient.query(cypher).bindAll(Map.of("id", id, "fieldsJson", fieldsJson)).run();
            updated++;
        }
        return ResponseEntity.ok("Updated " + updated + " nodes.");
    }
} 