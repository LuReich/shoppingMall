
package it.back.category.service;

import it.back.category.dto.CategoryTreeResponseDTO;
import it.back.category.dto.CategoryDTO;
import it.back.category.entity.CategoryEntity;
import it.back.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

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



    public List<CategoryTreeResponseDTO> getCategoryTree() {
        List<CategoryEntity> allCategories = categoryRepository.findAll();
        Map<Integer, CategoryTreeResponseDTO> categoryMap = allCategories.stream()
                .map(CategoryTreeResponseDTO::fromEntity)
                .collect(Collectors.toMap(CategoryTreeResponseDTO::getCategoryId, dto -> dto));

        List<CategoryTreeResponseDTO> rootCategories = new ArrayList<>();

        for (CategoryEntity category : allCategories) {
            CategoryTreeResponseDTO currentDTO = categoryMap.get(category.getCategoryId());
            if (category.getParent() == null) {
                rootCategories.add(currentDTO);
            } else {
                CategoryTreeResponseDTO parentDTO = categoryMap.get(category.getParent().getCategoryId());
                if (parentDTO != null) {
                    parentDTO.getChildren().add(currentDTO);
                }
            }
        }
        return rootCategories;
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
