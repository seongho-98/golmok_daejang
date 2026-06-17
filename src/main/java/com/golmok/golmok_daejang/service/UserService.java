package com.golmok.golmok_daejang.service;

import com.golmok.golmok_daejang.dto.BusinessTypeTransitionRow;
import com.golmok.golmok_daejang.dto.request.DogamSaveRequest;
import com.golmok.golmok_daejang.dto.response.BusinessTypeRecommendationData;
import com.golmok.golmok_daejang.dto.response.UserProfileData;
import com.golmok.golmok_daejang.entity.*;
import com.golmok.golmok_daejang.entity.enums.Rarity;
import com.golmok.golmok_daejang.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public BusinessTypeRecommendationData recommendBusinessTypes(String residentNumber) {
        List<BusinessTypeTransitionRow> rows = transactionHistoryRepository.findBusinessTypeTransitions(residentNumber);
        if (rows.isEmpty()) return BusinessTypeRecommendationData.builder().build();

        // A: 현재업종 중 빈도 1위 (동률이면 최근 거래 우선)
        String typeA = mostFrequent(rows, BusinessTypeTransitionRow::getCurrentType, Set.of());
        if (typeA == null) return BusinessTypeRecommendationData.builder().build();

        // B: A 다음 전환 업종 1위 → 없으면 현재업종 빈도에서 fallback (A 제외)
        String typeBFromTransition = mostFrequent(
            rows.stream().filter(r -> typeA.equals(r.getCurrentType()) && r.getNextType() != null).toList(),
            BusinessTypeTransitionRow::getNextType,
            Set.of(typeA)
        );
        String typeB = typeBFromTransition != null
                ? typeBFromTransition
                : mostFrequent(rows, BusinessTypeTransitionRow::getCurrentType, Set.of(typeA));

        // C: B 다음 전환 업종 1위 → 없으면 현재업종 빈도에서 fallback (A, B 제외)
        Set<String> excludedForC = typeB != null ? Set.of(typeA, typeB) : Set.of(typeA);
        String typeCFromTransition = typeB == null ? null : mostFrequent(
            rows.stream().filter(r -> typeB.equals(r.getCurrentType()) && r.getNextType() != null).toList(),
            BusinessTypeTransitionRow::getNextType,
            excludedForC
        );
        String typeC = typeCFromTransition != null
                ? typeCFromTransition
                : mostFrequent(rows, BusinessTypeTransitionRow::getCurrentType, excludedForC);

        return BusinessTypeRecommendationData.builder()
                .typeA(typeA)
                .storesA(storesByType(residentNumber, typeA))
                .typeB(typeB)
                .storesB(typeB != null ? storesByType(residentNumber, typeB) : List.of())
                .typeC(typeC)
                .storesC(typeC != null ? storesByType(residentNumber, typeC) : List.of())
                .build();
    }

    private List<String> storesByType(String residentNumber, String businessType) {
        return transactionHistoryRepository.findStoreNamesByTypeAndTimeFilter(residentNumber, businessType);
    }

    private String mostFrequent(List<BusinessTypeTransitionRow> rows,
                                 Function<BusinessTypeTransitionRow, String> typeExtractor,
                                 Set<String> excluded) {
        record TypeStats(long count, LocalDate date, LocalTime time) {}

        Map<String, TypeStats> statsMap = new HashMap<>();
        for (BusinessTypeTransitionRow row : rows) {
            String type = typeExtractor.apply(row);
            if (type == null || excluded.contains(type)) continue;
            LocalDate rowDate = row.getDate();
            LocalTime rowTime = row.getTime();
            statsMap.merge(type,
                new TypeStats(1, rowDate, rowTime),
                (existing, ignored) -> {
                    boolean isNewer = rowDate.isAfter(existing.date()) ||
                        (rowDate.equals(existing.date()) && rowTime.isAfter(existing.time()));
                    return new TypeStats(
                        existing.count() + 1,
                        isNewer ? rowDate : existing.date(),
                        isNewer ? rowTime : existing.time()
                    );
                }
            );
        }

        return statsMap.entrySet().stream()
            .max(Comparator
                .<Map.Entry<String, TypeStats>>comparingLong(e -> e.getValue().count())
                .thenComparing(e -> e.getValue().date())
                .thenComparing(e -> e.getValue().time()))
            .map(Map.Entry::getKey)
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
