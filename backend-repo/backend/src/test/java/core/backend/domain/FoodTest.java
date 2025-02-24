package core.backend.domain;

import java.util.List;
import core.backend.repository.FoodRepository;
import core.backend.repository.HeartRepository;
import core.backend.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class FoodTest {
    @Autowired
    FoodRepository foodRepository;
    @Autowired
    HeartRepository heartRepository;
    @Autowired
    MemberRepository memberRepository;
    @Test
    @DisplayName("Food-Heart")
    void teset() throws Exception {
        //given
        Member member = new Member().builder()
                .name("test_member")
                .email("test@test.com")
                .password("test")
                .role(RoleType.USER)
                .nationality("Test Nationality")
                .build();
        memberRepository.saveAndFlush(member);
        Food food1 = new Food().builder()
                .name("TestFood1")
                .build();
        Food food2 = new Food().builder()
                .name("TestFood2")
                .build();
        foodRepository.saveAllAndFlush(List.of(food1, food2));

        Heart heart1 = new Heart().builder()
                .food(food1)
                .member(member)
                .build();
        Heart heart2 = new Heart().builder()
            .food(food2)
            .member(member)
            .build();
        heartRepository.saveAllAndFlush(List.of(heart1, heart2));

        //when
        List<Heart> all = heartRepository.findAll();
        //then
        for (Heart heart : all) {
            System.out.println("heart.getFood(), heart.getMember()\n" + heart.getFood().getName() + " " + heart.getMember().getName());
        }

    }
}