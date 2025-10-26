package it.back.category.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import it.back.category.dto.CategoryDTO;
import it.back.category.entity.CategoryEntity;
import it.back.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDTO> getCategoryList() {
        List<CategoryEntity> allCategories = categoryRepository.findAll();
        return allCategories.stream().map(entity -> {
            CategoryDTO dto = new CategoryDTO();
            dto.setCategoryId(entity.getCategoryId());
            dto.setCategoryName(entity.getCategoryName());
            dto.setParentId(entity.getParent() != null ? entity.getParent().getCategoryId() : null);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<Integer> getCategoryWithChild(Integer categoryId) {
        List<CategoryEntity> allCategories = categoryRepository.findAll();
        Map<Integer, CategoryEntity> categoryEntityMap = allCategories.stream()
                .collect(Collectors.toMap(CategoryEntity::getCategoryId, category -> category));

        Set<Integer> resultIds = new HashSet<>();
        collectChildCategoryIds(categoryId, categoryEntityMap, resultIds);

        return new ArrayList<>(resultIds);
    }

    private void collectChildCategoryIds(Integer currentCategoryId, Map<Integer, CategoryEntity> categoryEntityMap, Set<Integer> collectedIds) {
        if (currentCategoryId == null || collectedIds.contains(currentCategoryId)) {
            return;
        }

        collectedIds.add(currentCategoryId);

        // Find children by iterating through the map and checking their parent_id
        for (CategoryEntity category : categoryEntityMap.values()) {
            if (category.getParent() != null && category.getParent().getCategoryId() != null && category.getParent().getCategoryId().equals(currentCategoryId)) {
                collectChildCategoryIds(category.getCategoryId(), categoryEntityMap, collectedIds);
            }
        }
    }
}
