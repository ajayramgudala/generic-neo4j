package com.example.genericneo4j.controller;

import com.example.genericneo4j.config.AuditLogger;
import com.example.genericneo4j.model.DocumentNode;
import com.example.genericneo4j.model.DocumentRelationship;
import com.example.genericneo4j.repository.DocumentNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/relationships")
public class DocumentRelationshipController {

    @Autowired
    private DocumentNodeRepository nodeRepository;

    @Autowired
    private AuditLogger auditLogger;

    // Create a relationship of a given type
    @PostMapping
    public DocumentNode createRelationship(@RequestParam Long fromId,
                                           @RequestParam Long toId,
                                           @RequestParam(defaultValue = "LINKED_TO") String type,
                                           @RequestBody Map<String, Object> properties) {
        Optional<DocumentNode> fromNodeOpt = nodeRepository.findById(fromId);
        Optional<DocumentNode> toNodeOpt = nodeRepository.findById(toId);
        if (fromNodeOpt.isEmpty() || toNodeOpt.isEmpty()) {
            throw new RuntimeException("Node(s) not found");
        }
        DocumentNode fromNode = fromNodeOpt.get();
        DocumentNode toNode = toNodeOpt.get();
        DocumentRelationship relationship = new DocumentRelationship(toNode, type, properties);
        if (fromNode.getRelationshipsByType() == null) {
            fromNode.setRelationshipsByType(new HashMap<>());
        }
        fromNode.getRelationshipsByType().computeIfAbsent(type, k -> new ArrayList<>()).add(relationship);
        nodeRepository.save(fromNode);
        auditLogger.log("CREATE", "RELATIONSHIP", relationship.getId(), relationship.getPropertiesJson());
        return fromNode;
    }

    // Get all relationships for a node (optionally by type)
    @GetMapping
    public List<DocumentRelationship> getAllRelationships(@RequestParam Long nodeId,
                                                         @RequestParam(required = false) String type) {
        Optional<DocumentNode> nodeOpt = nodeRepository.findById(nodeId);
        if (nodeOpt.isEmpty()) {
            throw new RuntimeException("Node not found");
        }
        DocumentNode node = nodeOpt.get();
        if (type != null) {
            return node.getRelationshipsByType() != null && node.getRelationshipsByType().containsKey(type)
                    ? node.getRelationshipsByType().get(type)
                    : Collections.emptyList();
        } else {
            return node.getAllRelationships();
        }
    }

    // Delete a relationship by ID
    @DeleteMapping("/{relationshipId}")
    public String deleteRelationship(@PathVariable Long relationshipId) {
        List<DocumentNode> allNodes = nodeRepository.findAll();
        for (DocumentNode node : allNodes) {
            if (node.getRelationshipsByType() != null) {
                for (List<DocumentRelationship> relList : node.getRelationshipsByType().values()) {
                    Iterator<DocumentRelationship> it = relList.iterator();
                    while (it.hasNext()) {
                        DocumentRelationship rel = it.next();
                        if (relationshipId.equals(rel.getId())) {
                            it.remove();
                            nodeRepository.save(node);
                            auditLogger.log("DELETE", "RELATIONSHIP", relationshipId, "");
                            return "Deleted relationship " + relationshipId;
                        }
                    }
                }
            }
        }
        return "Relationship not found";
    }

    // Update relationship properties by ID
    @PutMapping("/{relationshipId}")
    public String updateRelationship(@PathVariable Long relationshipId, @RequestBody Map<String, Object> properties) {
        List<DocumentNode> allNodes = nodeRepository.findAll();
        for (DocumentNode node : allNodes) {
            if (node.getRelationshipsByType() != null) {
                for (List<DocumentRelationship> relList : node.getRelationshipsByType().values()) {
                    for (DocumentRelationship rel : relList) {
                        if (relationshipId.equals(rel.getId())) {
                            rel.setProperties(properties);
                            nodeRepository.save(node);
                            auditLogger.log("UPDATE", "RELATIONSHIP", relationshipId, rel.getPropertiesJson());
                            return "Updated relationship " + relationshipId;
                        }
                    }
                }
            }
        }
        return "Relationship not found";
    }

    // Subgraph extraction: return all nodes and relationships reachable from a given node up to a certain depth
    @GetMapping("/subgraph")
    public Map<String, Object> extractSubgraph(@RequestParam Long startId, @RequestParam(defaultValue = "2") int depth) {
        Optional<DocumentNode> startOpt = nodeRepository.findById(startId);
        if (startOpt.isEmpty()) {
            throw new RuntimeException("Start node not found");
        }
        Set<Long> visited = new HashSet<>();
        List<DocumentNode> nodes = new ArrayList<>();
        List<DocumentRelationship> relationships = new ArrayList<>();
        Queue<DocumentNode> queue = new LinkedList<>();
        queue.add(startOpt.get());
        int currentDepth = 0;
        while (!queue.isEmpty() && currentDepth <= depth) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                DocumentNode node = queue.poll();
                if (node == null || visited.contains(node.getId())) continue;
                visited.add(node.getId());
                nodes.add(node);
                if (node.getRelationshipsByType() != null) {
                    for (List<DocumentRelationship> relList : node.getRelationshipsByType().values()) {
                        for (DocumentRelationship rel : relList) {
                            relationships.add(rel);
                            if (rel.getTarget() != null && !visited.contains(rel.getTarget().getId())) {
                                queue.add(rel.getTarget());
                            }
                        }
                    }
                }
            }
            currentDepth++;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("nodes", nodes.stream().map(DocumentNode::getId).collect(Collectors.toList()));
        result.put("relationships", relationships.stream().map(DocumentRelationship::getId).collect(Collectors.toList()));
        return result;
    }
} 