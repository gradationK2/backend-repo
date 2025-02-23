package core.backend.service;

import core.backend.domain.Member;
import core.backend.domain.RoleType;
import core.backend.jwt.JwtUtil;
import core.backend.oauth.OAuth2UserInfo;
import core.backend.oauth.OAuth2UserInfoFactory;
import core.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //OAuth2 제공자(구글) 에서 받은 사용자 정보 매핑
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes());

        if(userInfo.getEmail() == null){
            throw new OAuth2AuthenticationException("OAuth2 로그인 실패: 이메일 정보를 가져올 수 없습니다.");
        }

        //사용자 정보 조회, 신규 가입
        Member member = memberRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> registerNewUser(userInfo));

        //jwt 토큰 생성
        String accessToken = jwtUtil.generateToken(member);
        String refreshToken = jwtUtil.generateRefreshToken(member);
        member.setRefreshToken(refreshToken);
        memberRepository.save(member);

        System.out.println("Generated JWT Token: " + accessToken);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
                userInfo.getAttributes(),
                "email"
        );
    }

    private Member registerNewUser(OAuth2UserInfo userInfo){
        Member member = Member.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .password("")
                .nationality("UNKNOWN")
                .role(RoleType.USER)
                .build();
        return memberRepository.save(member);
    }
}
