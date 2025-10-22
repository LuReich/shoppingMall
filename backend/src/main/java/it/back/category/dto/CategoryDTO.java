package it.back.category.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Integer categoryId;
    private String categoryName;
    private Integer parentId;
}
