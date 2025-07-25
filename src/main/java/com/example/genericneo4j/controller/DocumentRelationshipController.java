package com.example.genericneo4j.controller;

import com.example.genericneo4j.model.DocumentNode;
import com.example.genericneo4j.model.DocumentRelationship;
import com.example.genericneo4j.repository.DocumentNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/relationships")
public class DocumentRelationshipController {

    @Autowired
    private DocumentNodeRepository nodeRepository;

    @PostMapping
    public DocumentNode createRelationship(@RequestParam Long fromId,
                                           @RequestParam Long toId,
                                           @RequestBody Map<String, Object> properties) {
        Optional<DocumentNode> fromNodeOpt = nodeRepository.findById(fromId);
        Optional<DocumentNode> toNodeOpt = nodeRepository.findById(toId);
        if (fromNodeOpt.isEmpty() || toNodeOpt.isEmpty()) {
            throw new RuntimeException("Node(s) not found");
        }
        DocumentNode fromNode = fromNodeOpt.get();
        DocumentNode toNode = toNodeOpt.get();
        DocumentRelationship relationship = new DocumentRelationship(toNode, properties);
        if (fromNode.getRelationships() == null) {
            fromNode.setRelationships(new ArrayList<>());
        }
        fromNode.getRelationships().add(relationship);
        nodeRepository.save(fromNode);
        return fromNode;
    }

    @GetMapping
    public List<DocumentRelationship> getAllRelationships(@RequestParam Long nodeId) {
        Optional<DocumentNode> nodeOpt = nodeRepository.findById(nodeId);
        if (nodeOpt.isEmpty()) {
            throw new RuntimeException("Node not found");
        }
        DocumentNode node = nodeOpt.get();
        return node.getRelationships() != null ? node.getRelationships() : Collections.emptyList();
    }
} 