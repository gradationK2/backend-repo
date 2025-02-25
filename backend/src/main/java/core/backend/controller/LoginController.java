package core.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    // 로그인 성공 시 프론트로 리다이렉트
    @GetMapping("/success")
    public ResponseEntity<Map<String, String>> loginSuccess(@RequestParam(required = false) String accessToken,
                                                            @RequestParam(required = false) String refreshToken) {
        return ResponseEntity.ok(Map.of(
                "message", "로그인 성공",
                "accessToken", accessToken != null ? accessToken : "N/A",
                "refreshToken", refreshToken != null ? refreshToken : "N/A"
        ));
    }

    // 로그인 실패 시 프론트로 리다이렉트
    @GetMapping("/failed")
    public ResponseEntity<Map<String, String>> loginFailed() {
        return ResponseEntity.badRequest().body(Map.of("error", "로그인 실패"));
    }
}

