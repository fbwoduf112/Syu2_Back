package com.example.demo.store.controller;

import com.example.demo.store.dto.StoreLocationResponse;
import com.example.demo.store.entity.Store;
import com.example.demo.store.entity.StoreLocation;
import com.example.demo.store.service.StoreLocationService;
import com.example.demo.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/stores/address")
@RequiredArgsConstructor
public class StoreAddressController {
    private final StoreLocationService storeLocationService;
    private final StoreService storeService;

    @Operation(
        summary = "상점 주소 저장/수정",
        description = "상점의 주소, 위도, 경도 등 위치 정보를 저장하거나 수정합니다."
    )
    @PostMapping
    public ResponseEntity<?> updateOrCreateStoreLocation(
            @RequestParam Long storeId,
            @RequestParam String fullAddress,
            @RequestParam String city,
            @RequestParam String district,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        Store store = storeService.findById(storeId);
        storeLocationService.saveOrUpdateLocation(store, fullAddress, city, district, latitude, longitude);
        return ResponseEntity.ok().body("Store location saved/updated successfully");
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreLocation(@PathVariable Long storeId, @AuthenticationPrincipal Store store) {
        try {
            if (store == null || store.getStoreId() != storeId) {
                return ResponseEntity.status(403).body("접근 권한이 없습니다.");
            }
            Optional<StoreLocation> location = storeLocationService.findByStore(store);
            if (location.isPresent()) {
                // fullAddress만 반환
                return ResponseEntity.ok(StoreLocationResponse.from(location.get().getFullAddress()));
            } else {
                return ResponseEntity.ok().body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("주소 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}