package com.golmok.golmok_daejang.service;

import com.golmok.golmok_daejang.dto.BusinessTypeTransitionRow;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CharacterInfoRepository characterInfoRepository;
    private final DogamRepository dogamRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

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

    @Transactional(readOnly = true)
    public List<String> recommendBusinessTypes(String residentNumber) {
        List<BusinessTypeTransitionRow> rows = transactionHistoryRepository.findBusinessTypeTransitions(residentNumber);
        if (rows.isEmpty()) return List.of();

        // A: 현재업종 중 빈도 1위
        String typeA = mostFrequent(
            rows.stream().map(BusinessTypeTransitionRow::getCurrentType).toList(),
            Set.of()
        );
        if (typeA == null) return List.of();

        // B: A 다음에 가장 많이 나온 업종 (A 제외)
        String typeB = mostFrequent(
            rows.stream()
                .filter(r -> typeA.equals(r.getCurrentType()) && r.getNextType() != null)
                .map(BusinessTypeTransitionRow::getNextType)
                .toList(),
            Set.of(typeA)
        );
        if (typeB == null) return List.of(typeA);

        // C: B 다음에 가장 많이 나온 업종 (A, B 제외)
        String typeC = mostFrequent(
            rows.stream()
                .filter(r -> typeB.equals(r.getCurrentType()) && r.getNextType() != null)
                .map(BusinessTypeTransitionRow::getNextType)
                .toList(),
            Set.of(typeA, typeB)
        );

        return typeC != null ? List.of(typeA, typeB, typeC) : List.of(typeA, typeB);
    }

    private String mostFrequent(List<String> types, Set<String> excluded) {
        return types.stream()
            .filter(t -> !excluded.contains(t))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet().stream()
            .max(java.util.Map.Entry.comparingByValue())
            .map(java.util.Map.Entry::getKey)
            .orElse(null);
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
