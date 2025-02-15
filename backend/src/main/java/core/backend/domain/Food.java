package core.backend.domain;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties({"hibernateLAzyInitializer", "handler", "reviews"}) // hibernate프록시 무시
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")//ID기반 직렬화
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // 음식 이름 (한글명)

    @Column(nullable = false, unique = true)
    private String englishName; // 영어 이름

    @Lob // 필드 타입 String일 시 CLOB으로 매핑
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Min(0)
    @Column(nullable = false)
    private Integer scoville;

    @Column(nullable = false)
    private String category; // 음식 카테고리 (sort->category 수정)

    @Column(nullable = false)
    private String imgUrl;

    @JsonManagedReference // 무한 루프 방지
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private final List<Review> reviews = new ArrayList<>(); // 리뷰 리스트

    //좋아요 리스트(Hearts와 연결)
    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private final List<Heart> hearts = new ArrayList<>(); // 좋아요 개수 정보
    

    //좋아요 개수를 반환
    public int getHeartCount(){
        return hearts != null ? hearts.size() : 0;
    }
}
