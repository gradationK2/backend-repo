package core.backend.jpamapping;

import core.backend.domain.Food;
import core.backend.domain.Heart;
import core.backend.domain.Member;
import core.backend.domain.RoleType;
import core.backend.repository.FoodRepository;
import core.backend.repository.HeartRepository;
import core.backend.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.hibernate.validator.internal.constraintvalidators.bv.time.pastorpresent.PastOrPresentValidatorForCalendar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class JpaTest {
    @Autowired FoodRepository foodRepository;
    @Autowired HeartRepository heartRepository;
    @Autowired MemberRepository memberRepository;

    @Test
    @DisplayName("연관관계 테스트")
    void test() throws Exception {
        //given
        // food가 아닌 heart가 주인


        // when


        // then
//        assertNotNull(food);
//        assertEquals(3, hearts.size());
//        assertTrue(hearts.stream().allMatch(heart -> heart.getFood().equals(updatedfood)));
    }

}