package it.back.seller.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import it.back.common.pagination.PageRequestDTO;
import it.back.seller.dto.SellerPublicDTO;
import it.back.seller.dto.SellerPublicListDTO;

public interface SellerRepositoryCustom {
    Page<SellerPublicListDTO> findSellerPublicList(PageRequestDTO pageRequestDTO, Long sellerUid, String companyName,
            String businessRegistrationNumber, String phone, String address);

    Optional<SellerPublicDTO> findSellerPublicInfoById(Long sellerUid);
}
