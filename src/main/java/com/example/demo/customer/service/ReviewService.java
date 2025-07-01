package com.example.demo.customer.service;

import com.example.demo.order.entity.OrderGroup;
import com.example.demo.order.repository.OrderGroupRepository;
import com.example.demo.setting.util.S3UploadService;
import com.example.demo.customer.dto.CustomerReviewDto;
import com.example.demo.customer.dto.ReviewWriteDTO;
import com.example.demo.customer.dto.UnreviewedStatisticsDto;
import com.example.demo.customer.entity.CustomerReviewCollect;
import com.example.demo.customer.entity.CustomerStatistics;
import com.example.demo.customer.entity.Customer;
import com.example.demo.store.entity.Store;
import com.example.demo.store.entity.StoreMenu;
import com.example.demo.customer.repository.CustomerReviewCollectRepository;
import com.example.demo.customer.repository.CustomerStatisticsRepository;
import com.example.demo.store.repository.StoreMenuRepository;
import com.example.demo.store.repository.StoreRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final CustomerStatisticsRepository customerStatisticsRepository;
    private final S3UploadService s3UploadService;
    private final CustomerReviewCollectRepository customerReviewCollectRepository;
    private final StoreRepository storeRepository;
    private final StoreMenuRepository storeMenuRepository;
    private final OrderGroupRepository orderGroupRepository;

    @PersistenceContext
    private EntityManager em;


    public List<UnreviewedStatisticsDto> getUnreviewedStatisticsByCustomer(Customer customer) {
        List<CustomerStatistics> unreviewedList = customerStatisticsRepository.findByCustomerAndReviewedFalse(customer);

        return unreviewedList.stream()
                .filter(stat -> !"UserPointUsedOrNotUsed".equals(stat.getOrderDetails())) // 이 조건을 추가
                .map(stat -> new UnreviewedStatisticsDto(
                        stat.getId(),
                        stat.getStore().getStoreName(),
                        stat.getOrderDetails(),
                        stat.getOrderPrice(),
                        stat.getOrderAmount(),
                        stat.getDate()
                ))
                .toList();
    }

    @Transactional
    public void saveReview(Customer customer, ReviewWriteDTO reviewWriteDTO) {
        CustomerStatistics customerStatistics = customerStatisticsRepository.findById(reviewWriteDTO.getStatisticsId()).orElse(null);
        Store store = customerStatistics.getStore();
        StoreMenu storeMenu = storeMenuRepository.findByMenuName(customerStatistics.getOrderDetails());

        CustomerReviewCollect reviewCollect = reviewWriteDTO.toEntity(customer, store, customerStatistics, storeMenu);
        customerReviewCollectRepository.save(reviewCollect);
        customerStatistics.markAsReviewed();

        storeMenu.updateRating(reviewWriteDTO.getReviewRating());
    }

    public List<CustomerReviewDto> getReviewsByMenu(Long menuId) {
        List<CustomerReviewCollect> reviews = customerReviewCollectRepository.findByStoreMenu_MenuId(menuId);
        return reviews.stream()
                .map(CustomerReviewDto::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteReview(Customer customer) {
        List<OrderGroup> toDelete = orderGroupRepository.findAllByCustomerAndApproved(customer, false);

        for (OrderGroup orderGroup : toDelete) {
            // 연관된 CustomerStatistics 먼저 삭제 (연관관계가 orphanRemoval=false라면 필수)
            customerStatisticsRepository.deleteAll(orderGroup.getCustomerStatisticsList());
        }

        // 그 다음 OrderGroup 자체 삭제
        orderGroupRepository.deleteAll(toDelete);
    }

}