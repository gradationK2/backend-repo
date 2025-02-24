package core.backend.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Heart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="heart_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="food_id", nullable = false)
    private Food food;

    @ManyToOne
    @JoinColumn(name="member_id", nullable = false)
    private Member member;
}
