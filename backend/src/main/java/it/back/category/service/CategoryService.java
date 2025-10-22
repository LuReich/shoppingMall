package it.back.category.service;

import it.back.category.dto.CategoryTreeDTO;
import it.back.category.entity.CategoryEntity;
import it.back.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import it.back.category.dto.CategoryDTO; // Added this

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    private List<CategoryEntity> getAllCategoriesInternal() {
        return categoryRepository.findAll();
    }

    @Transactional
    public List<CategoryDTO> getAllCategoriesFlat() {
        return getAllCategoriesInternal().stream()
                .map(category -> {
                    CategoryDTO dto = new CategoryDTO();
                    dto.setCategoryId(category.getCategoryId());
                    dto.setCategoryName(category.getCategoryName());
                    if (category.getParent() != null) {
                        dto.setParentId(category.getParent().getCategoryId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CategoryTreeDTO> getCategoryTree() {
        List<CategoryEntity> allCategories = getAllCategoriesInternal();

        Map<Integer, CategoryTreeDTO> categoryMap = allCategories.stream()
                .map(category -> CategoryTreeDTO.builder()
                        .categoryId(category.getCategoryId())
                        .categoryName(category.getCategoryName())
                        .children(new ArrayList<CategoryTreeDTO>()) // Initialize children list
                        .build())
                .collect(Collectors.toMap(CategoryTreeDTO::getCategoryId, dto -> dto));

        List<CategoryTreeDTO> rootCategories = new ArrayList<>();

        allCategories.forEach(categoryEntity -> {
            CategoryTreeDTO categoryDTO = categoryMap.get(categoryEntity.getCategoryId());
            if (categoryEntity.getParent() == null) {
                rootCategories.add(categoryDTO);
            } else {
                CategoryTreeDTO parentDTO = categoryMap.get(categoryEntity.getParent().getCategoryId());
                if (parentDTO != null) {
                    parentDTO.getChildren().add(categoryDTO);
                }
            }
        });

        return rootCategories;
    }

    public List<Integer> getCategoryWithChild(Integer parentId) {
        List<Integer> allCategoryIds = new java.util.ArrayList<>();
        if (parentId != null) {
            allCategoryIds.add(parentId);
            // In-memory recursive search
            List<CategoryEntity> allCategories = getAllCategoriesInternal();
            findDescendantIds(parentId, allCategoryIds, allCategories);
        }
        return allCategoryIds;
    }

    private void findDescendantIds(Integer parentId, List<Integer> allCategoryIds, List<CategoryEntity> allCategories) {
        List<CategoryEntity> children = allCategories.stream()
                .filter(c -> c.getParent() != null && c.getParent().getCategoryId().equals(parentId))
                .toList();

        for (CategoryEntity child : children) {
            allCategoryIds.add(child.getCategoryId());
            findDescendantIds(child.getCategoryId(), allCategoryIds, allCategories);
        }
    }
}