package com.example.genericneo4j.controller;

import com.example.genericneo4j.config.AuditLogger;
import com.example.genericneo4j.model.DocumentNode;
import com.example.genericneo4j.repository.DocumentNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/nodes")
public class DocumentNodeController {

    @Autowired
    private DocumentNodeRepository nodeRepository;

    @Autowired
    private AuditLogger auditLogger;

    @PostMapping
    public DocumentNode createNode(@RequestBody DocumentNode node) {
        node.setFields(node.getFields()); // ensure fieldsJson is set
        DocumentNode saved = nodeRepository.save(node);
        saved.setFields(saved.getFields()); // ensure fields is populated for response
        auditLogger.log("CREATE", "NODE", saved.getId(), saved.getFieldsJson());
        return saved;
    }

    @GetMapping("/{id}")
    public Optional<DocumentNode> getNode(@PathVariable Long id) {
        return nodeRepository.findById(id);
    }

    @GetMapping
    public List<DocumentNode> getAllNodes() {
        return nodeRepository.findAll();
    }

    @PutMapping("/{id}")
    public DocumentNode updateNode(@PathVariable Long id, @RequestBody DocumentNode node) {
        node.setId(id);
        node.setFields(node.getFields()); // ensure fieldsJson is set
        DocumentNode saved = nodeRepository.save(node);
        saved.setFields(saved.getFields()); // ensure fields is populated for response
        auditLogger.log("UPDATE", "NODE", saved.getId(), saved.getFieldsJson());
        return saved;
    }

    @DeleteMapping("/{id}")
    public void deleteNode(@PathVariable Long id) {
        nodeRepository.deleteById(id);
        auditLogger.log("DELETE", "NODE", id, "");
    }
} 