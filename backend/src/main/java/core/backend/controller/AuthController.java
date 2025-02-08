package core.backend.controller;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


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
        //이미 존재하는 사용자 확인
        if(memberRepository.findByEmail(request.getEmail()).isPresent()){
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        //새로운 사용자 객체 생성, 저장
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nationality(request.getNationality())
                .role(RoleType.USER)
                .build();

        memberRepository.save(member);

        return "회원가입 성공";
    }

    //로그인 API(JWT 발급)
    @PostMapping("/login")
    public String login(@Valid @RequestBody MemberLoginRequest request){
        //이메일로 사용자 조회
        Member foundMember = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //비밀번호 확인
        if(!passwordEncoder.matches(request.getPassword(), foundMember.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        //jwt 토큰 발급 후 반환
        return jwtUtil.generateToken(foundMember.getEmail(), foundMember.getRole().name());
    }

    //로그아웃 API(클라이언트에서 JWT 삭제)
    @PostMapping("/logout")
    public String logout(){
        return "로그아웃 성공";
    }
}
