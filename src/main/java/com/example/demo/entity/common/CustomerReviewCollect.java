package com.example.demo.entity.common;

import com.example.demo.entity.customer.Customer;
import com.example.demo.entity.store.Store;
import com.example.demo.entity.store.StoreMenu;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CustomerReviewCollect {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double score;

    private String reviewDetails;

    private LocalDate reviewDate;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id") // 외래키 이름
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id") // 외래키 이름
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id") // 외래키 이름
    private StoreMenu storeMenu;
}
