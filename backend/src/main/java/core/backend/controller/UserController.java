package core.backend.controller;

import core.backend.dto.PasswordChangeRequest;
import core.backend.domain.BadgeType;
import core.backend.dto.UserProfileUpdateRequest;
import core.backend.exception.CustomException;
import core.backend.exception.ErrorCode;
import core.backend.jwt.JwtUtil;
import core.backend.repository.MemberRepository;
import core.backend.service.HeartService;
import core.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import core.backend.domain.Member;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final HeartService heartService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //현재 로그인한 사용자 프로필 수정
    @PutMapping("/me")
    public Map<String, String> updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute UserProfileUpdateRequest updateRequest)  {
        if (updateRequest == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        //현재 로그인한 사용자 찾기
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        //입력된 값이 null이 아닐 경우에만 업데이트
        if (updateRequest.getName() != null && updateRequest.getName().trim().isEmpty()) {
            member.setName(updateRequest.getName());
        }
        if (updateRequest.getNationality() != null && updateRequest.getNationality().trim().isEmpty()) {
            member.setNationality(updateRequest.getNationality());
        }
        if (updateRequest.getImage() != null && !updateRequest.getImage().isEmpty()) {
            String savedPath = memberService.updateProfileImage(member, updateRequest.getImage());
            member.setPhotoUrl(savedPath);
        }

        //변경된 정보 저장
        memberRepository.save(member);

        //json형식의 응답 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "프로필 수정 완료");
        return response;
    }

    @GetMapping("/me") // 프론트에서 정보를 합쳐달라고 하여 수정함
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
    // 비밀번호 수정
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody PasswordChangeRequest request){

        //jwt애서 이메일 추출
        String email = jwtUtil.extractEmail(token.replace("Bearer", ""));
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        //현재 비밀번호 확인
        if(!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        //새 비밀번호 같은지 검사
        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        //비밀번호 변경, 저장
        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        memberRepository.save(member);

        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    
}
