package it.back.category.controller;

import java.util.List; // Changed import

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.back.category.dto.CategoryTreeDTO;
import it.back.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
// Removed java.util.stream.Collectors as it's no longer needed for manual mapping

import it.back.category.dto.CategoryDTO; // Added this

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<List<CategoryTreeDTO>> getAllCategories() {
        List<CategoryTreeDTO> categoryTree = categoryService.getCategoryTree();
        return ResponseEntity.ok(categoryTree);
    }

    @GetMapping("/flat-list") // New endpoint
    public ResponseEntity<List<CategoryDTO>> getAllCategoriesFlat() {
        List<CategoryDTO> categories = categoryService.getAllCategoriesFlat();
        return ResponseEntity.ok(categories);
    }
}
