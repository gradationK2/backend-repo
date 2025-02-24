package core.backend.service;

import java.util.List;
import core.backend.domain.Food;
import core.backend.domain.Heart;
import core.backend.domain.Member;
import core.backend.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartService {
    private final HeartRepository heartRepository;

    public List<Heart> getHeartsByUser(Member member) {
        return heartRepository.findAllByMember(member);
    }


    public boolean isLiked(Member member, Food food) {
        return heartRepository.existsByFoodAndMember(food, member);
    }

    public void addUser(Member member, Food food) {
        heartRepository.save(Heart.builder()
                .member(member)
                .food(food)
                .build());
    }

    public void deleteUser(Member member, Food food) {
        heartRepository.deleteByFoodAndMember(food, member);
    }

    public List<Heart> getHeartsByFood(Food food) {
        return heartRepository.findAllByFood(food);
    }
}
