package com.example.genericneo4j.controller;

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

    @PostMapping
    public DocumentNode createNode(@RequestBody DocumentNode node) {
        return nodeRepository.save(node);
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
        return nodeRepository.save(node);
    }

    @DeleteMapping("/{id}")
    public void deleteNode(@PathVariable Long id) {
        nodeRepository.deleteById(id);
    }
} 