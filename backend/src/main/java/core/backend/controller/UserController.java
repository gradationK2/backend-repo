package core.backend.controller;

import core.backend.domain.BadgeType;
import core.backend.dto.UserProfileUpdateRequest;
import core.backend.repository.HeartRepository;
import core.backend.repository.MemberRepository;
import core.backend.service.HeartService;
import core.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final HeartService heartService;
    private final MemberService memberService;
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

    @GetMapping("/me/advance")
    public ResponseEntity<?> getUserProfileAdvanced(@AuthenticationPrincipal UserDetails userDetails) {
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Number userPercent = memberService.getUserPercent(member.getId());
        int reviews = member.getReviews().size();

        Map<String, String> response = new HashMap<>();
        response.put("id", String.valueOf(member.getId()));
        response.put("email", member.getEmail());
        response.put("name", member.getName());
        response.put("role", member.getRole().name());
        response.put("nationality", member.getNationality());
        response.put("createDate", member.getCreateDate().toString());
        response.put("badge", member.getBadge().name());
        response.put("profileImagePath", member.getPhotoUrl() != null ? member.getPhotoUrl() : "");
        response.put("reviewCount", String.valueOf(reviews));
        response.put("heartCount", String.valueOf(heartService.getHeartsByUser(member).size())); // TODO : member는 hearts 가지고 있지 않았나?
        response.put("badgeCount", String.valueOf(BadgeType.getBadgeCount(reviews)));
        response.put("percent", String.valueOf(userPercent));

        return ResponseEntity.ok().body(response);
    }
}
