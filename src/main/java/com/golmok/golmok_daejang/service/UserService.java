package com.golmok.golmok_daejang.service;

import com.golmok.golmok_daejang.dto.request.DogamSaveRequest;
import com.golmok.golmok_daejang.dto.response.UserProfileData;
import com.golmok.golmok_daejang.entity.*;
import com.golmok.golmok_daejang.entity.enums.Rarity;
import com.golmok.golmok_daejang.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CharacterInfoRepository characterInfoRepository;
    private final DogamRepository dogamRepository;

    @Transactional(readOnly = true)
    public UserProfileData getProfile(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        List<Dogam> dogamList = dogamRepository.findByUser(user);

        return UserProfileData.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .type("개인")
                .rarity1(toCharacterItems(dogamList, Rarity.COMMON))
                .rarity2(toCharacterItems(dogamList, Rarity.RARE))
                .rarity3(toCharacterItems(dogamList, Rarity.LEGENDARY))
                .createdAt(user.getCreatedAt())
                .build();
    }

    private List<UserProfileData.CharacterItem> toCharacterItems(List<Dogam> dogamList, Rarity rarity) {
        return dogamList.stream()
                .map(d -> characterInfoRepository.findByBusinessInfo(d.getBusinessInfo()).orElse(null))
                .filter(c -> c != null && c.getRarity() == rarity)
                .map(c -> UserProfileData.CharacterItem.builder()
                        .imgUrl(c.getCharacterUrl())
                        .characterName(c.getName())
                        .businessName(c.getBusinessInfo().getBusinessName())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveDogam(DogamSaveRequest req) {
        User user = userRepository.findByLoginId(req.getLoginId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        for (Long characterId : req.getCharacterIds()) {
            CharacterInfo character = characterInfoRepository.findById(characterId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 캐릭터입니다: " + characterId));

            BusinessInfo business = character.getBusinessInfo();

            if (!dogamRepository.existsByUserAndBusinessInfo(user, business)) {
                Dogam dogam = new Dogam();
                dogam.setUser(user);
                dogam.setBusinessInfo(business);
                dogam.setCollectionDate(LocalDate.now());
                dogamRepository.save(dogam);
            }
        }
    }
}
