package core.backend.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "members")
@JsonIgnoreProperties({"hibernateLAzyInitializer", "handler", "reviews"}) // hibernate프록시 무시
public class Member {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    @Column(unique = true, nullable = false) // 중복 방지, 필수값 설정
    private String email;

    @Column(unique = true)
    private String name;

    @Column(nullable = false) // 필수값 설정
    @JsonIgnore // api응답에서 비밀번호 필드 제외
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(nullable = false)
    @Builder.Default
    private String nationality = "UNKNOWN"; // 기본값 설정

    @Column(updatable = false) // 회원가입 시 자동 생성(수정 불가)
    @CreationTimestamp // 쿼리 Insert 시 현재시간 저장
    private LocalDateTime createDate;

    @Enumerated(EnumType.STRING)
    private BadgeType badge;

    private String photoUrl;

    private String refreshToken;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private final List<Review> reviews = new ArrayList<>();

    //refresh token 업데이트
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

}
