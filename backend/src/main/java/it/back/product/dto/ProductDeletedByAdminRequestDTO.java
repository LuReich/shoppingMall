package it.back.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDeletedByAdminRequestDTO {
    private Boolean isDeleted;
    private String deletedByAdminReason;
    private String deletedBySellerReason; // Allow admin to modify seller's reason
}
