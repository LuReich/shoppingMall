
package it.back.category.controller;

import it.back.category.dto.CategoryDTO;
import it.back.category.dto.CategoryTreeResponseDTO;
import it.back.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import it.back.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategoryList() {

        List<CategoryDTO> categoryList = categoryService.getCategoryList();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(categoryList));
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponseDTO>>> getCategoryTree() {

        List<CategoryTreeResponseDTO> categoryTree = categoryService.getCategoryTree();
        
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(categoryTree));
    }

}
