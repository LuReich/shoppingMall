package it.back.category.dto;

import it.back.category.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeResponseDTO {
    
    private int categoryId;
    private String categoryName;
    private List<CategoryTreeResponseDTO> children;

    public static CategoryTreeResponseDTO fromEntity(CategoryEntity categoryEntity) {
        return CategoryTreeResponseDTO.builder()
                .categoryId(categoryEntity.getCategoryId())
                .categoryName(categoryEntity.getCategoryName())
                .children(new ArrayList<>()) // Initialize children list
                .build();
    }
}
