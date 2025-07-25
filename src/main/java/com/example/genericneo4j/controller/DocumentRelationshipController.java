package com.example.genericneo4j.controller;

import com.example.genericneo4j.model.DocumentNode;
import com.example.genericneo4j.model.DocumentRelationship;
import com.example.genericneo4j.repository.DocumentNodeRepository;
import com.example.genericneo4j.repository.DocumentRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/relationships")
public class DocumentRelationshipController {

    @Autowired
    private DocumentRelationshipRepository relationshipRepository;

    @Autowired
    private DocumentNodeRepository nodeRepository;

    @PostMapping
    public DocumentRelationship createRelationship(@RequestParam Long fromId,
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
        // Attach to fromNode (if you want to model outgoing relationships)
        // This is a simple save for the relationship
        return relationshipRepository.save(relationship);
    }

    @GetMapping
    public List<DocumentRelationship> getAllRelationships() {
        return relationshipRepository.findAll();
    }
} 