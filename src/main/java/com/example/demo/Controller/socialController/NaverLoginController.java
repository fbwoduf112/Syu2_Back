package com.example.demo.Controller.socialController;

import com.example.demo.Service.NaverLoginService;
import com.example.demo.entity.customer.Customer;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.repository.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Tag(name = "네이버 로그인", description = "네이버 소셜 로그인 관련 API")
public class NaverLoginController {
    private final NaverLoginService naverLoginService;
    private final CustomerRepository customerRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${naver.redirect_uri}")
    private String naverRedirectUri;

    @Value("${naver.client_id}")
    private String naverClientId;

    @Operation(summary = "네이버 로그인 콜백", description = "네이버 인증 후 콜백을 처리합니다.")
    @GetMapping("/login/naver")
    public ResponseEntity<?> naverCallback(
            @Parameter(description = "인증 코드") @RequestParam String code, 
            @Parameter(description = "상태 값") @RequestParam String state) {
        String tokenResponse = naverLoginService.getNaverAccessToken(code, state); // 네이버 토큰 요청 메서드 호출

        Optional<Customer> optionalCustomer = customerRepository.findByEmail(tokenResponse);

        if (optionalCustomer.isEmpty()) {
            Customer newCustomer = Customer.builder()
                    .email(tokenResponse)
                    .provider("NAVER")
                    .build();
            customerRepository.save(newCustomer);
            log.info("신규 회원 등록 완료");
        } else {
            log.info("기존 회원입니다.");
        }
        String jwt = jwtTokenProvider.createToken(tokenResponse);

        ResponseCookie cookie = ResponseCookie.from("access_token", jwt)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Set-Cookie", cookie.toString())
                .header("Location", "http://localhost:3000/")
                .build();
    }

    @Operation(summary = "네이버 로그인 리다이렉트", description = "네이버 로그인 페이지로 리다이렉트합니다.")
    @GetMapping("/api/oauth2/naver/login")
    public ResponseEntity<Void> redirectToNaver() {
        String naverAuthUrl = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=" +
                naverClientId +
                "&state=1234" +
                "&redirect_uri=" +
                naverRedirectUri;

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, naverAuthUrl)
                .build();
    }
}
