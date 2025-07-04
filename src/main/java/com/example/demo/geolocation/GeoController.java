package com.example.demo.geolocation;

import com.example.demo.benefit.dto.CouponDto;
import com.example.demo.benefit.service.CustomerCouponService;
import com.example.demo.setting.util.IpExtraction;
import com.example.demo.socialLogin.controller.KakaoLoginController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GeoController {
    private final GeoService geoService;
    private final CustomerCouponService customerCouponService;
    @GetMapping("/location")
    public ResponseEntity<List<GeoResponseStoreDto>> findAroundStore(@ModelAttribute GeoRequestDto geoRequestDto) {
        SimpleAddressDto result = geoService.requestGeolocation(geoRequestDto);
        List<GeoResponseStoreDto> geoResponseStoreDto = geoService.findStore(result);
        return ResponseEntity.ok(geoResponseStoreDto);
    }

    @GetMapping("/location/coupon")
    public ResponseEntity<?> findAroundStoreCoupon(@ModelAttribute GeoRequestDto geoRequestDto) {
        SimpleAddressDto result = geoService.requestGeolocation(geoRequestDto);
        List<CouponDto> availableCoupons = geoService.getAllAvailableCoupons(result);
         return ResponseEntity.ok(availableCoupons);
    }



}
