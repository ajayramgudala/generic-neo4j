package com.example.genericneo4j.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/graph")
public class GraphAlgorithmController {

    @Autowired
    private Neo4jClient neo4jClient;

    // Shortest path using Cypher
    @GetMapping("/shortest-path")
    public List<Long> shortestPath(@RequestParam Long fromId, @RequestParam Long toId) {
        String cypher = "MATCH (start:DocumentNode), (end:DocumentNode) " +
                "WHERE id(start) = $fromId AND id(end) = $toId " +
                "MATCH path = shortestPath((start)-[*..100]-(end)) " +
                "RETURN [n IN nodes(path) | id(n)] AS nodeIds";
        return neo4jClient.query(cypher)
                .bindAll(Map.of("fromId", fromId, "toId", toId))
                .fetchAs(List.class)
                .one()
                .orElse(Collections.emptyList());
    }

    // BFS traversal (returns visited node IDs in order)
    @GetMapping("/bfs")
    public List<Long> bfs(@RequestParam Long startId, @RequestParam(defaultValue = "10") int depth) {
        String cypher = "MATCH (start:DocumentNode) WHERE id(start) = $startId " +
                "CALL apoc.path.spanningTree(start, {maxLevel: $depth}) YIELD path " +
                "UNWIND nodes(path) AS n RETURN DISTINCT id(n) AS nodeId";
        return neo4jClient.query(cypher)
                .bindAll(Map.of("startId", startId, "depth", depth))
                .fetch().all().stream()
                .map(m -> (Long) m.get("nodeId"))
                .toList();
    }

    // DFS traversal (returns visited node IDs in order)
    @GetMapping("/dfs")
    public List<Long> dfs(@RequestParam Long startId, @RequestParam(defaultValue = "10") int depth) {
        String cypher = "MATCH (start:DocumentNode) WHERE id(start) = $startId " +
                "CALL apoc.path.spanningTree(start, {maxLevel: $depth, bfs:false}) YIELD path " +
                "UNWIND nodes(path) AS n RETURN DISTINCT id(n) AS nodeId";
        return neo4jClient.query(cypher)
                .bindAll(Map.of("startId", startId, "depth", depth))
                .fetch().all().stream()
                .map(m -> (Long) m.get("nodeId"))
                .toList();
    }

    // Example: PageRank (requires APOC or GDS plugin)
    @GetMapping("/pagerank")
    public List<Map<String, Object>> pageRank(@RequestParam(defaultValue = "10") int limit) {
        String cypher = "CALL gds.pageRank.stream({nodeProjection:'DocumentNode', relationshipProjection:{LINKED_TO:{type:'LINKED_TO', orientation:'NATURAL'}}}) " +
                "YIELD nodeId, score RETURN gds.util.asNode(nodeId).id AS id, score ORDER BY score DESC LIMIT $limit";
        return new ArrayList<>(neo4jClient.query(cypher)
                .bindAll(Map.of("limit", limit))
                .fetch().all());
    }

    // Connected Components (Weakly Connected)
    @GetMapping("/connected-components")
    public List<Map<String, Object>> connectedComponents() {
        String cypher = "CALL gds.wcc.stream({nodeProjection:'DocumentNode', relationshipProjection:{LINKED_TO:{type:'LINKED_TO', orientation:'NATURAL'}}}) " +
                "YIELD nodeId, componentId RETURN gds.util.asNode(nodeId).id AS id, componentId ORDER BY componentId";
        return new ArrayList<>(neo4jClient.query(cypher).fetch().all());
    }

    // Betweenness Centrality
    @GetMapping("/betweenness-centrality")
    public List<Map<String, Object>> betweennessCentrality(@RequestParam(defaultValue = "10") int limit) {
        String cypher = "CALL gds.betweenness.stream({nodeProjection:'DocumentNode', relationshipProjection:{LINKED_TO:{type:'LINKED_TO', orientation:'NATURAL'}}}) " +
                "YIELD nodeId, score RETURN gds.util.asNode(nodeId).id AS id, score ORDER BY score DESC LIMIT $limit";
        return new ArrayList<>(neo4jClient.query(cypher).bindAll(Map.of("limit", limit)).fetch().all());
    }

    // Closeness Centrality
    @GetMapping("/closeness-centrality")
    public List<Map<String, Object>> closenessCentrality(@RequestParam(defaultValue = "10") int limit) {
        String cypher = "CALL gds.alpha.closeness.stream({nodeProjection:'DocumentNode', relationshipProjection:{LINKED_TO:{type:'LINKED_TO', orientation:'NATURAL'}}}) " +
                "YIELD nodeId, centrality RETURN gds.util.asNode(nodeId).id AS id, centrality ORDER BY centrality DESC LIMIT $limit";
        return new ArrayList<>(neo4jClient.query(cypher).bindAll(Map.of("limit", limit)).fetch().all());
    }

    // Community Detection (Louvain)
    @GetMapping("/community-louvain")
    public List<Map<String, Object>> communityLouvain() {
        String cypher = "CALL gds.louvain.stream({nodeProjection:'DocumentNode', relationshipProjection:{LINKED_TO:{type:'LINKED_TO', orientation:'NATURAL'}}}) " +
                "YIELD nodeId, communityId RETURN gds.util.asNode(nodeId).id AS id, communityId ORDER BY communityId";
        return new ArrayList<>(neo4jClient.query(cypher).fetch().all());
    }

    // Community Detection (Label Propagation)
    @GetMapping("/community-label-propagation")
    public List<Map<String, Object>> communityLabelPropagation() {
        String cypher = "CALL gds.labelPropagation.stream({nodeProjection:'DocumentNode', relationshipProjection:{LINKED_TO:{type:'LINKED_TO', orientation:'NATURAL'}}}) " +
                "YIELD nodeId, communityId RETURN gds.util.asNode(nodeId).id AS id, communityId ORDER BY communityId";
        return new ArrayList<>(neo4jClient.query(cypher).fetch().all());
    }

    // Node Similarity (Jaccard)
    @GetMapping("/similarity")
    public List<Map<String, Object>> nodeSimilarity(@RequestParam(defaultValue = "10") int limit) {
        String cypher = "CALL gds.nodeSimilarity.stream({nodeProjection:'DocumentNode', relationshipProjection:{LINKED_TO:{type:'LINKED_TO', orientation:'NATURAL'}}}) " +
                "YIELD node1, node2, similarity RETURN gds.util.asNode(node1).id AS node1, gds.util.asNode(node2).id AS node2, similarity ORDER BY similarity DESC LIMIT $limit";
        return new ArrayList<>(neo4jClient.query(cypher).bindAll(Map.of("limit", limit)).fetch().all());
    }

    // Triangle Counting
    @GetMapping("/triangle-count")
    public List<Map<String, Object>> triangleCount(@RequestParam(defaultValue = "10") int limit) {
        String cypher = "CALL gds.triangleCount.stream({nodeProjection:'DocumentNode', relationshipProjection:{LINKED_TO:{type:'LINKED_TO', orientation:'NATURAL'}}}) " +
                "YIELD nodeId, triangleCount RETURN gds.util.asNode(nodeId).id AS id, triangleCount ORDER BY triangleCount DESC LIMIT $limit";
        return new ArrayList<>(neo4jClient.query(cypher).bindAll(Map.of("limit", limit)).fetch().all());
    }

    // Graph Embeddings (FastRP)
    @GetMapping("/embedding-fastrp")
    public List<Map<String, Object>> embeddingFastRP(@RequestParam(defaultValue = "10") int limit) {
        String cypher = "CALL gds.fastRP.stream({nodeProjection:'DocumentNode', relationshipProjection:{LINKED_TO:{type:'LINKED_TO', orientation:'NATURAL'}}}) " +
                "YIELD nodeId, embedding RETURN gds.util.asNode(nodeId).id AS id, embedding ORDER BY id LIMIT $limit";
        return new ArrayList<>(neo4jClient.query(cypher).bindAll(Map.of("limit", limit)).fetch().all());
    }
} 