package com.example.demo.benefit.repository;

import com.example.demo.benefit.entity.Coupon;
import com.example.demo.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
    Optional<Coupon> findByIdWithPessimisticLock(@Param("id") Long id);

    @Query("SELECT c FROM Coupon c WHERE c.store = :store AND c.status = 'ACTIVE' " +
            "AND (c.totalQuantity > c.issuedQuantity) " +
            "AND (c.issueStartTime IS NULL OR c.issueStartTime <= CURRENT_TIMESTAMP)")
    List<Coupon> findAvailableCouponsByStore(@Param("store") Store store);

    @Query("SELECT c FROM Coupon c WHERE c.status = 'ACTIVE' " +
            "AND (c.totalQuantity > c.issuedQuantity) " +
            "AND (c.issueStartTime IS NULL OR c.issueStartTime <= CURRENT_TIMESTAMP)")
    List<Coupon> findAllAvailableCoupons();

    // ID와 스토어로 쿠폰 찾기
    Optional<Coupon> findByIdAndStore(Long id, Store store);

    List<Coupon> findAllByStore(Store store);

    // 만료된 Coupon 엔티티 삭제 (Cascade로 관련 CustomerCoupon도 함께 삭제됨)
    @Modifying
    @Query("DELETE FROM Coupon c WHERE c.expiryDate < :now AND c.expiryDate IS NOT NULL")
    int deleteByExpiryDateBefore(@Param("now") LocalDateTime now);
}
