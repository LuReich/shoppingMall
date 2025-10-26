package it.back.category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.back.category.dto.CategoryDTO;
import it.back.category.service.CategoryService;
import it.back.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;

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

}
