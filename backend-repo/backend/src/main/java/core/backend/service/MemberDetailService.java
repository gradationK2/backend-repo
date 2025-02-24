package core.backend.service;

import core.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import core.backend.domain.Member;
import java.util.Optional;


// spring security에서 사용자 인증을 위해 사용되는 서비스
@Service // spring bean으로 등록
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {
    // 회원 정보 불러오는 서비스
    private final MemberRepository memberRepository;

    //사용자의 이메일 기반으로 회원정보 조회 메서드
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Optional<Member> member = memberRepository.findByEmail(email);

        if(member.isEmpty()){
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        //spring security에서 자동으로 "ROLe_"을 추가하므로 직접 붙이지 말기
        String role = member.get().getRole().name();

        return User.builder()
                .username(member.get().getEmail())
                .password(member.get().getPassword())
                .roles(role)
                .build();
    }
}
