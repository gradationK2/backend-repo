package core.backend.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLAzyInitializer", "handler"}) // hibernate프록시 무시
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // 지연 로딩 설정
    @JoinColumn(name="food_id", nullable = false)
    @JsonBackReference //자식 관계 review->food
    private Food food; //food와 연관관계

    @ManyToOne(fetch = FetchType.EAGER) // 지연 로딩 설정
    @JoinColumn(name="member_id", nullable = false)
    @JsonIgnoreProperties({"reviews"})
    private Member member; //member와 연관관계

    @Lob // 긴 문자열을 저장할 때 사용
    @Column(columnDefinition = "TEXt", nullable = false)
    private String content;

    private Integer rating;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    private Integer upvote;

    private Integer downvote;

    
    @Column(nullable = false)
    private Integer spicyLevel; // 1~5단계 매운맛 평가
}
