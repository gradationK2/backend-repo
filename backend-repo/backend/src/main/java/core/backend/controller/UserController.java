package core.backend.controller;

import core.backend.dto.UserProfileUpdateRequest;
import core.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import core.backend.domain.Member;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final MemberRepository memberRepository;

    //현재 로그인한 사용자 프로필 조회
    @GetMapping("/me")
    public Member getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }

    //현재 로그인한 사용자 프로필 수정
    @PutMapping("/me")
    public Map<String, String> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserProfileUpdateRequest updateRequest) {

        //현재 로그인한 사용자 찾기
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        //입력된 값이 null이 아닐 경우에만 업데이트
        if (updateRequest.getName() != null) member.setName(updateRequest.getName());
        if (updateRequest.getPhotoUrl() != null) member.setPhotoUrl(updateRequest.getPhotoUrl());
        if (updateRequest.getNationality() != null) member.setNationality(updateRequest.getNationality());

        //변경된 정보 저장
        memberRepository.save(member);

        //json형식의 응답 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "프로필 수정 완료");
        return response;
    }

    
}
