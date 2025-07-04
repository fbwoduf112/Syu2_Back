package com.example.demo.order.entity;
import com.example.demo.customer.entity.Customer;
import com.example.demo.store.entity.Store;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class OrderStatus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String status;

    private String menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id") // 외래키 이름
    @JsonBackReference
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id") // 외래키 이름
    @JsonBackReference("store-order-status")
    private Store store;

}
