package it.back.order.service;

import it.back.order.entity.OrderEntity;
import it.back.order.entity.OrderDetailEntity;
import it.back.order.repository.OrderRepository;
import it.back.order.repository.OrderDetailRepository;
import it.back.order.dto.OrderResponseDTO;
import it.back.order.dto.OrderDTO;
import it.back.order.dto.OrderDetailDTO;
import it.back.order.dto.OrderDetailSellerResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import it.back.common.pagination.PageResponseDTO;
import it.back.order.specification.OrderDetailSpecifications;
import it.back.product.entity.ProductEntity;
import it.back.product.repository.ProductRepository;
import it.back.seller.repository.SellerRepository;

@Service
@RequiredArgsConstructor

public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final it.back.buyer.repository.BuyerDetailRepository buyerDetailRepository;

    @Transactional
    public OrderResponseDTO createOrder(OrderDTO orderDTO) {
        OrderEntity order = new OrderEntity();
        order.setBuyerUid(orderDTO.getBuyerUid());
        // buyerUid로 BuyerDetailEntity에서 phone 조회 후 세팅
        buyerDetailRepository.findByBuyerUid(orderDTO.getBuyerUid())
                .ifPresentOrElse(
                        detail -> order.setBuyerPhone(detail.getPhone()),
                        () -> {
                            throw new IllegalArgumentException("구매자 상세 정보(전화번호)를 찾을 수 없습니다.");
                        }
                );
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setRecipientName(orderDTO.getRecipientName());
        order.setRecipientAddress(orderDTO.getRecipientAddress());
        order.setRecipientAddressDetail(orderDTO.getRecipientAddressDetail());
        order.setOrderStatus(OrderEntity.OrderStatus.valueOf(orderDTO.getOrderStatus()));

        if (orderDTO.getOrderDetails() != null) {
            List<OrderDetailEntity> details = orderDTO.getOrderDetails().stream().map(detailDTO -> {
                OrderDetailEntity detail = new OrderDetailEntity();
                detail.setProductId(detailDTO.getProductId());
                detail.setSellerUid(detailDTO.getSellerUid());
                detail.setQuantity(detailDTO.getQuantity());
                detail.setPricePerItem(detailDTO.getPricePerItem());
                detail.setOrderDetailStatus(OrderDetailEntity.OrderDetailStatus.valueOf(detailDTO.getOrderDetailStatus()));
                detail.setOrderDetailStatusReason(detailDTO.getOrderDetailStatusReason());
                detail.setOrder(order);
                return detail;
            }).collect(Collectors.toList());
            order.setOrderDetails(details);
        }
        OrderEntity saved = orderRepository.save(order);
        // 상품/판매자 정보 맵핑을 위해 id 수집
        List<Long> productIds = saved.getOrderDetails().stream().map(OrderDetailEntity::getProductId).distinct().toList();
        List<Long> sellerUids = saved.getOrderDetails().stream().map(OrderDetailEntity::getSellerUid).distinct().toList();
        Map<Long, ProductEntity> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(p -> p.getProductId(), p -> p));
        Map<Long, String> companyNameMap = sellerRepository.findAllById(sellerUids).stream()
                .collect(Collectors.toMap(s -> s.getSellerUid(), s -> s.getCompanyName()));

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(saved.getOrderId());
        dto.setCreateAt(saved.getCreateAt());
        dto.setUpdateAt(saved.getUpdateAt());
        dto.setRecipientName(saved.getRecipientName());
        dto.setRecipientAddress(saved.getRecipientAddress());
        dto.setRecipientAddressDetail(saved.getRecipientAddressDetail());
        dto.setBuyerPhone(saved.getBuyerPhone());
        dto.setStatus(saved.getOrderStatus().name());
        dto.setTotalPrice(saved.getTotalPrice());
        // 주문상세 전체를 리스트로 매핑 + 상품/판매자 정보 + 주문/상세 생성일/수정일 포함
        if (saved.getOrderDetails() != null) {
            List<OrderDetailDTO> detailDTOs = saved.getOrderDetails().stream().map(detail -> {
                OrderDetailDTO d = new OrderDetailDTO();
                d.setOrderDetailId(detail.getOrderDetailId());
                d.setProductId(detail.getProductId());
                d.setSellerUid(detail.getSellerUid());
                d.setQuantity(detail.getQuantity());
                d.setPricePerItem(detail.getPricePerItem());
                d.setOrderDetailStatus(detail.getOrderDetailStatus() != null ? detail.getOrderDetailStatus().name() : null);
                                            d.setOrderDetailStatusReason(detail.getOrderDetailStatusReason());                var product = productMap.get(detail.getProductId());
                if (product != null) {
                    d.setProductName(product.getProductName());
                    d.setProductThumbnailUrl(product.getThumbnailUrl());
                }
                d.setCompanyName(companyNameMap.get(detail.getSellerUid()));
                d.setCreateAt(detail.getCreateAt());
                d.setUpdateAt(detail.getUpdateAt());
                return d;
            }).collect(Collectors.toList());
            dto.setOrderDetails(detailDTOs);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<OrderResponseDTO> getOrdersByBuyerUid(Long buyerUid, Pageable pageable) {
        // 1. Repository에서 buyerUid로 직접 조회
        Page<OrderEntity> page = orderRepository.findByBuyerUid(buyerUid, pageable);

        // 전체 주문상세에 대한 productId, sellerUid 수집
        List<OrderDetailEntity> allDetails = page.getContent().stream()
                .flatMap(order -> order.getOrderDetails().stream())
                .toList();
        List<Long> productIds = allDetails.stream().map(OrderDetailEntity::getProductId).distinct().toList();
        List<Long> sellerUids = allDetails.stream().map(OrderDetailEntity::getSellerUid).distinct().toList();
        Map<Long, ProductEntity> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(p -> p.getProductId(), p -> p));
        Map<Long, String> companyNameMap = sellerRepository.findAllById(sellerUids).stream()
                .collect(Collectors.toMap(s -> s.getSellerUid(), s -> s.getCompanyName()));

        // 2. 메모리 필터링(.filter) 제거
        List<OrderResponseDTO> dtoList = page.getContent().stream()
                .map(order -> {
                    OrderResponseDTO dto = new OrderResponseDTO();
                    dto.setOrderId(order.getOrderId());
                    dto.setCreateAt(order.getCreateAt());
                    dto.setUpdateAt(order.getUpdateAt());
                    dto.setRecipientName(order.getRecipientName());
                    dto.setRecipientAddress(order.getRecipientAddress());
                    dto.setRecipientAddressDetail(order.getRecipientAddressDetail());
                    dto.setBuyerPhone(order.getBuyerPhone());
                    dto.setStatus(order.getOrderStatus().name());
                    dto.setTotalPrice(order.getTotalPrice());
                    if (order.getOrderDetails() != null) {
                        List<OrderDetailDTO> detailDTOs = order.getOrderDetails().stream().map(detail -> {
                            OrderDetailDTO d = new OrderDetailDTO();
                            d.setOrderDetailId(detail.getOrderDetailId());
                            d.setProductId(detail.getProductId());
                            d.setSellerUid(detail.getSellerUid());
                            d.setQuantity(detail.getQuantity());
                            d.setPricePerItem(detail.getPricePerItem());
                            d.setOrderDetailStatus(detail.getOrderDetailStatus() != null ? detail.getOrderDetailStatus().name() : null);
                            d.setOrderDetailStatusReason(detail.getOrderDetailStatusReason());
                            var product = productMap.get(detail.getProductId());
                            if (product != null) {
                                d.setProductName(product.getProductName());
                                d.setProductThumbnailUrl(product.getThumbnailUrl());
                            }
                            d.setCompanyName(companyNameMap.get(detail.getSellerUid()));
                            d.setCreateAt(detail.getCreateAt());
                            d.setUpdateAt(detail.getUpdateAt());
                            return d;
                        }).collect(Collectors.toList());
                        dto.setOrderDetails(detailDTOs);
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        // 3. 올바른 정보가 담긴 page 객체를 사용
        return new PageResponseDTO<>(
                dtoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    // toDTO, toDetailDTO 제거: 변환 책임을 DTO로 이동
    /**
     * 주문상세 전체가 DELIVERED면 주문도 DELIVERED로 상태 변경 판매자가 주문 상태 변경 시 자동으로 체크
     */
    @Transactional
    public void updateOrderStatusIfAllDelivered(Long orderId) {
        List<OrderDetailEntity> details = orderDetailRepository.findByOrderOrderId(orderId);
        boolean allDelivered = details.stream()
                .allMatch(d -> d.getOrderDetailStatus() == OrderDetailEntity.OrderDetailStatus.DELIVERED);
        if (allDelivered && !details.isEmpty()) {
            OrderEntity order = orderRepository.findById(orderId).orElse(null);
            if (order != null && order.getOrderStatus() != OrderEntity.OrderStatus.DELIVERED) {
                order.setOrderStatus(OrderEntity.OrderStatus.DELIVERED);
                orderRepository.save(order);
            }
        }
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<it.back.order.dto.OrderDetailSellerResponseDTO> getSellerOrderDetails(
            Long sellerUid,
            Pageable pageable,
            String productName,
            Long productId,
            Integer categoryId,
            String orderDetailStatus,
            String recipientName,
            String recipientPhone,
            String recipientAddress,
            String recipientAddressDetail) {

        Specification<OrderDetailEntity> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction(); // Start with an always-true predicate

            predicate = criteriaBuilder.and(predicate, OrderDetailSpecifications.hasSellerUid(sellerUid).toPredicate(root, query, criteriaBuilder));
            predicate = criteriaBuilder.and(predicate, OrderDetailSpecifications.hasCategoryId(categoryId).toPredicate(root, query, criteriaBuilder));
            predicate = criteriaBuilder.and(predicate, OrderDetailSpecifications.hasProductId(productId).toPredicate(root, query, criteriaBuilder));
            predicate = criteriaBuilder.and(predicate, OrderDetailSpecifications.hasProductName(productName).toPredicate(root, query, criteriaBuilder));
            predicate = criteriaBuilder.and(predicate, OrderDetailSpecifications.hasOrderDetailStatus(orderDetailStatus).toPredicate(root, query, criteriaBuilder));
            predicate = criteriaBuilder.and(predicate, OrderDetailSpecifications.hasRecipientName(recipientName).toPredicate(root, query, criteriaBuilder));
            predicate = criteriaBuilder.and(predicate, OrderDetailSpecifications.hasRecipientPhone(recipientPhone).toPredicate(root, query, criteriaBuilder));
            predicate = criteriaBuilder.and(predicate, OrderDetailSpecifications.hasRecipientAddress(recipientAddress).toPredicate(root, query, criteriaBuilder));
            predicate = criteriaBuilder.and(predicate, OrderDetailSpecifications.hasRecipientAddressDetail(recipientAddressDetail).toPredicate(root, query, criteriaBuilder));

            return predicate;
        };

        Page<OrderDetailEntity> page = orderDetailRepository.findAll(spec, pageable);

        // 상품 및 판매자 정보 매핑을 위한 ID 수집
        List<Long> productIds = page.getContent().stream().map(OrderDetailEntity::getProductId).distinct().toList();
        Map<Long, ProductEntity> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(p -> p.getProductId(), p -> p));

        // OrderDetailDTO로 변환
        List<it.back.order.dto.OrderDetailSellerResponseDTO> dtoList = page.getContent().stream()
                .map(detail -> {
                    it.back.order.dto.OrderDetailSellerResponseDTO dto = new it.back.order.dto.OrderDetailSellerResponseDTO();
                    dto.setOrderDetailId(detail.getOrderDetailId());
                    dto.setProductId(detail.getProductId());
                    dto.setQuantity(detail.getQuantity());
                    dto.setPricePerItem(detail.getPricePerItem());
                    dto.setOrderDetailStatus(detail.getOrderDetailStatus() != null ? detail.getOrderDetailStatus().name() : null);
                    dto.setOrderDetailStatusReason(detail.getOrderDetailStatusReason());

                    var product = productMap.get(detail.getProductId());
                    if (product != null) {
                        dto.setProductName(product.getProductName());
                        dto.setProductThumbnailUrl(product.getThumbnailUrl());
                    }
                    sellerRepository.findById(detail.getSellerUid()).ifPresent(seller -> {
                        dto.setCompanyName(seller.getCompanyName());
                    });

                    // Recipient Info from OrderEntity
                    OrderEntity order = detail.getOrder();
                    if (order != null) {
                        dto.setRecipientName(order.getRecipientName());
                        dto.setRecipientPhone(order.getBuyerPhone());
                        dto.setRecipientAddress(order.getRecipientAddress());
                        dto.setRecipientAddressDetail(order.getRecipientAddressDetail());
                    }

                    dto.setCreateAt(detail.getCreateAt());
                    dto.setUpdateAt(detail.getUpdateAt());
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                dtoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional
    public OrderDetailSellerResponseDTO updateOrderDetailStatus(Long sellerUid, Long orderDetailId, String newStatus, String statusReason) {
        OrderDetailEntity orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("주문 상세를 찾을 수 없습니다: " + orderDetailId));

        if (!orderDetail.getSellerUid().equals(sellerUid)) {
            throw new SecurityException("해당 주문 상세를 수정할 권한이 없습니다.");
        }

        // 상태 업데이트
        orderDetail.setOrderDetailStatus(OrderDetailEntity.OrderDetailStatus.valueOf(newStatus.toUpperCase()));
        orderDetail.setOrderDetailStatusReason(statusReason);
        orderDetailRepository.save(orderDetail);

        // 메인 주문 상태 업데이트 확인
        updateOrderStatusIfAllDelivered(orderDetail.getOrder().getOrderId());

        // 업데이트된 OrderDetailSellerResponseDTO 반환
        OrderDetailSellerResponseDTO dto = new OrderDetailSellerResponseDTO();
        dto.setOrderDetailId(orderDetail.getOrderDetailId());
        dto.setProductId(orderDetail.getProductId());
        dto.setQuantity(orderDetail.getQuantity());
        dto.setPricePerItem(orderDetail.getPricePerItem());
        dto.setOrderDetailStatus(orderDetail.getOrderDetailStatus() != null ? orderDetail.getOrderDetailStatus().name() : null);
        dto.setOrderDetailStatusReason(orderDetail.getOrderDetailStatusReason());

        // 상품 정보 매핑
        productRepository.findById(orderDetail.getProductId()).ifPresent(product -> {
            dto.setProductName(product.getProductName());
            dto.setProductThumbnailUrl(product.getThumbnailUrl());
        });

        // 판매자 정보 매핑
        sellerRepository.findById(orderDetail.getSellerUid()).ifPresent(seller -> {
            dto.setCompanyName(seller.getCompanyName());
        });

        // 수령인 정보 매핑 (OrderEntity에서)
        OrderEntity order = orderDetail.getOrder();
        if (order != null) {
            dto.setRecipientName(order.getRecipientName());
            dto.setRecipientPhone(order.getBuyerPhone());
            dto.setRecipientAddress(order.getRecipientAddress());
            dto.setRecipientAddressDetail(order.getRecipientAddressDetail());
        }

        dto.setCreateAt(orderDetail.getCreateAt());
        dto.setUpdateAt(orderDetail.getUpdateAt());

        return dto;
    }
}
