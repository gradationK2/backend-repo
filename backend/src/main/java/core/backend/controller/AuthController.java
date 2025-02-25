package core.backend.controller;

import core.backend.domain.BadgeType;
import core.backend.domain.RoleType;
import core.backend.jwt.JwtUtil;
import core.backend.dto.MemberSignupRequest;
import core.backend.dto.MemberLoginRequest;
import core.backend.repository.MemberRepository;
import core.backend.domain.Member;
import core.backend.exception.CustomException;
import core.backend.exception.ErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


// 인증(회원가입, 로그인, 로그아웃) API 컨트롤러
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //회원가입 API
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody MemberSignupRequest request){
        try {
            //이미 존재하는 이메일 확인
            if (request.getEmail() != null && memberRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            // 이미 존재하는 닉네임 확인
            if (request.getName() != null && memberRepository.findByName(request.getName()).isPresent()) {
                throw new CustomException(ErrorCode.NAME_ALREADY_EXISTS);
            }
        //새로운 사용자 객체 생성, 저장
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nationality(request.getNationality())
                .role(RoleType.USER)
                .badge(BadgeType.REVIEW_0)
                .build();

            memberRepository.save(member);

            return "회원가입 성공";
        }catch (DataIntegrityViolationException e){
            throw new CustomException(ErrorCode.NAME_ALREADY_EXISTS);
        }
    }

    //이메일 중복 확인 API
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam(name = "email") String email){
        if(memberRepository.findByEmail(email).isPresent()){
            return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
        }
        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }

    //이메일 중복 확인 requestbody방식 추가
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmailPost(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("이메일 값이 필요합니다.");
        }
        if (memberRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
        }
        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }

    //닉네임 중복 확인 API
    @GetMapping("/check-name")
    public ResponseEntity<?> checkName(@RequestParam(name = "name") String name){
        if(memberRepository.findByName(name).isPresent()){
            return ResponseEntity.badRequest().body("이미 사용 중인 닉네임입니다.");
        }
        return ResponseEntity.ok("사용 가능한 닉네임입니다.");
    }

    //닉네임 중복 확인 requestbody방식 추가
    @PostMapping("/check-name")
    public ResponseEntity<?> checkNamePost(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body("닉네임 값이 필요합니다.");
        }
        if (memberRepository.findByName(name).isPresent()) {
            return ResponseEntity.badRequest().body("이미 사용 중인 닉네임입니다.");
        }
        return ResponseEntity.ok("사용 가능한 닉네임입니다.");
    }

    //로그인 API(JWT 발급)
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody MemberLoginRequest request){
        //이메일로 사용자 조회
        Member foundMember = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //비밀번호 확인
        if(!passwordEncoder.matches(request.getPassword(), foundMember.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.generateToken(foundMember);
        String refreshToken = jwtUtil.generateRefreshToken(foundMember);

        // refresh token db에 저장
        foundMember.setRefreshToken(refreshToken);
        memberRepository.save(foundMember);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens);
    }

    // 자동 로그인
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");

        if(!jwtUtil.validateToken(refreshToken)){
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        //사용자 조회
        String email = jwtUtil.extractEmail(refreshToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!refreshToken.equals(member.getRefreshToken())){
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        //새 access token 생성
        String newAccessToken = jwtUtil.generateToken(member);
        String newRefreshToken = jwtUtil.generateRefreshToken(member);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        response.put("refreshToken", newRefreshToken);

        return ResponseEntity.ok(response);
    }

    //로그아웃 API(클라이언트에서 JWT 삭제)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request){
        String refreshToken = request.get("refreshToken");

        String email = jwtUtil.extractEmail(refreshToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //refresh token 제거
        member.updateRefreshToken(null);
        memberRepository.save(member);

        return ResponseEntity.ok("로그아웃 성공");
    }
}
