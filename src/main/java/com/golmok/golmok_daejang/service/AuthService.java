package com.golmok.golmok_daejang.service;

import com.golmok.golmok_daejang.dto.request.BusinessSignupRequest;
import com.golmok.golmok_daejang.dto.request.BusinessVerifyRequest;
import com.golmok.golmok_daejang.dto.request.LoginRequest;
import com.golmok.golmok_daejang.dto.request.UserSignupRequest;
import com.golmok.golmok_daejang.dto.response.*;
import com.golmok.golmok_daejang.entity.BusinessInfo;
import com.golmok.golmok_daejang.entity.CharacterInfo;
import com.golmok.golmok_daejang.entity.User;
import com.golmok.golmok_daejang.entity.enums.Rarity;
import com.golmok.golmok_daejang.entity.enums.Role;
import com.golmok.golmok_daejang.repository.BusinessInfoRepository;
import com.golmok.golmok_daejang.repository.CharacterInfoRepository;
import com.golmok.golmok_daejang.repository.TransactionHistoryRepository;
import com.golmok.golmok_daejang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BusinessInfoRepository businessInfoRepository;
    private final CharacterInfoRepository characterInfoRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final FileUploadService fileUploadService;

    @Transactional(readOnly = true)
    public LoginData loginUser(LoginRequest req) {
        User user = userRepository.findByLoginIdAndPassword(req.getLoginId(), req.getPassword())
                .filter(u -> u.getRole() == Role.USER)
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다."));

        return LoginData.builder()
                .user(LoginData.UserInfo.builder()
                        .loginId(user.getLoginId())
                        .name(user.getName())
                        .type("개인")
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public LoginData loginBusiness(LoginRequest req) {
        User user = userRepository.findByLoginIdAndPassword(req.getLoginId(), req.getPassword())
                .filter(u -> u.getRole() == Role.BUSINESS_OWNER)
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다."));

        BusinessInfo business = businessInfoRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("사업자 정보가 존재하지 않습니다."));

        return LoginData.builder()
                .user(LoginData.UserInfo.builder()
                        .loginId(user.getLoginId())
                        .name(user.getName())
                        .type("사업자")
                        .businessNumber(business.getBusinessNumber())
                        .build())
                .build();
    }

    @Transactional
    public UserSignupData signupUser(UserSignupRequest req) {
        if (userRepository.existsByLoginId(req.getLoginId())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByResidentNumber(req.getResidentNumber())) {
            throw new RuntimeException("이미 가입된 주민번호입니다.");
        }

        User user = new User();
        user.setLoginId(req.getLoginId());
        user.setPassword(req.getPassword());
        user.setName(req.getName());
        user.setResidentNumber(req.getResidentNumber());
        user.setRole(Role.USER);
        userRepository.save(user);

        return UserSignupData.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .type("개인")
                .build();
    }

    @Transactional(readOnly = true)
    public BusinessVerifyData verifyBusiness(BusinessVerifyRequest req) {
        // 거래내역에서 해당 주민번호와 연관된 사업자 조회
        List<BusinessInfo> businesses = transactionHistoryRepository
                .findDistinctBusinessesByResidentNumber(req.getResidentNumber());

        List<BusinessVerifyData.BusinessItem> items = businesses.stream()
                .map(b -> BusinessVerifyData.BusinessItem.builder()
                        .businessName(b.getBusinessName())
                        .businessNumber(b.getBusinessNumber())
                        .address(b.getAddress())
                        .businessType(b.getBusinessType())
                        .ownerName(req.getName()) // 외부 API 연동 전 요청자 이름으로 대체
                        .openDate(null)           // 외부 API 연동 전 미제공
                        .build())
                .collect(Collectors.toList());

        return BusinessVerifyData.builder().businesses(items).build();
    }

    @Transactional
    public BusinessSignupData signupBusiness(BusinessSignupRequest req) {
        if (userRepository.existsByLoginId(req.getLoginId())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByResidentNumber(req.getResidentNumber())) {
            throw new RuntimeException("이미 가입된 주민번호입니다.");
        }

        // 사용자 생성
        User user = new User();
        user.setLoginId(req.getLoginId());
        user.setPassword(req.getPassword());
        user.setName(req.getName());
        user.setResidentNumber(req.getResidentNumber());
        user.setRole(Role.BUSINESS_OWNER);
        userRepository.save(user);

        // 사업자 정보 생성
        BusinessInfo business = new BusinessInfo();
        business.setBusinessNumber(req.getBusinessNumber());
        business.setUser(user);
        business.setBusinessName(req.getBusinessName());
        business.setBusinessType(req.getBusinessType());
        business.setAddress(""); // 소재지는 본인확인 단계에서 전달받도록 추후 추가
        businessInfoRepository.save(business);

        // 캐릭터 이미지 저장 및 캐릭터 생성
        String imgUrl = fileUploadService.saveBase64Image(req.getCharacterFile());

        CharacterInfo character = new CharacterInfo();
        character.setUser(user);
        character.setBusinessInfo(business);
        character.setCharacterUrl(imgUrl);
        character.setName(req.getCharacterName());
        character.setRarity(Rarity.of(req.getRarity()));
        characterInfoRepository.save(character);

        return BusinessSignupData.builder()
                .loginId(user.getLoginId())
                .businessNumber(business.getBusinessNumber())
                .character(BusinessSignupData.CharacterInfo.builder()
                        .characterId(character.getCharacterId())
                        .name(character.getName())
                        .imgUrl(imgUrl)
                        .rarity(req.getRarity())
                        .build())
                .build();
    }
}
