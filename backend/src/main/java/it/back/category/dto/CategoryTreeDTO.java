package it.back.category.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CategoryTreeDTO {
    private Integer categoryId;
    private String categoryName;
    private List<CategoryTreeDTO> children;
}
