# 골목대장 API 명세서

**Base URL:** `http://localhost:8080`
**Content-Type:** `application/json`

---

## 공통 응답 형식

```json
{
  "success": true,
  "data": { ... }
}
```

---

## 1. 인증 (Auth)

### 1-1. 개인 로그인

- **POST** `/api/auth/login/user`

**Request**
```json
{
  "loginId": "string",
  "password": "string"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "user": {
      "loginId": "string",
      "name": "string",
      "type": "개인"
    }
  }
}
```

---

### 1-2. 사업자 로그인

- **POST** `/api/auth/login/business`

**Request**
```json
{
  "loginId": "string",
  "password": "string"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "user": {
      "loginId": "string",
      "name": "string",
      "type": "사업자",
      "businessNumber": "string"
    }
  }
}
```

---

### 1-3. 개인 회원가입

- **POST** `/api/auth/signup/user`

**Request**
```json
{
  "loginId": "string",
  "password": "string",
  "name": "string",
  "residentNumber": "string"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "loginId": "string",
    "name": "string",
    "type": "개인"
  }
}
```

---

### 1-4. 사업자 본인확인

- **POST** `/api/auth/verify/business`

**Request**
```json
{
  "name": "string",
  "residentNumber": "string"
}
```

**Response**
```json
{
  "success": true,
  "data": {
    "businesses": [
      {
        "businessName": "string",
        "businessNumber": "string",
        "address": "string",
        "businessType": "string",
        "ownerName": "string",
        "openDate": "string | null"
      }
    ]
  }
}
```

> `businesses`는 배열입니다. `openDate`는 외부 API 연동 전까지 `null`로 반환됩니다.

---

### 1-5. 사업자 회원가입

- **POST** `/api/auth/signup/business`

**Request**
```json
{
  "loginId": "string",
  "password": "string",
  "name": "string",
  "residentNumber": "string",
  "businessNumber": "string",
  "businessName": "string",
  "businessType": "string",
  "characterFile": "string (Base64 인코딩 이미지)",
  "characterName": "string",
  "rarity": 1
}
```

> `rarity`: 1 (일반) / 2 (레어) / 3 (전설)

**Response**
```json
{
  "success": true,
  "data": {
    "loginId": "string",
    "businessNumber": "string",
    "character": {
      "characterId": 1,
      "name": "string",
      "imgUrl": "string",
      "rarity": 1
    }
  }
}
```

---

## 2. 사용자 (User)

### 2-1. 내 프로필 조회 (개인)

- **GET** `/api/users/profile?id={loginId}`

**Query Parameter**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `id` | string | 로그인 아이디 |

**Response**
```json
{
  "success": true,
  "data": {
    "loginId": "string",
    "name": "string",
    "type": "개인",
    "rarity1": [
      { "imgUrl": "string", "characterName": "string", "businessName": "string" }
    ],
    "rarity2": [
      { "imgUrl": "string", "characterName": "string", "businessName": "string" }
    ],
    "rarity3": [
      { "imgUrl": "string", "characterName": "string", "businessName": "string" }
    ],
    "createdAt": "2026-06-15"
  }
}
```

> `rarity1/2/3`은 각각 희귀도 1/2/3에 해당하는 수집된 캐릭터 배열입니다.

---

### 2-2. 도감 저장

- **POST** `/api/dogam`

**Request**
```json
{
  "loginId": "string",
  "characterIds": [1, 2, 3]
}
```

> `characterIds`는 저장할 캐릭터 ID의 배열입니다. 이미 수집된 캐릭터는 자동으로 중복 저장되지 않습니다.

**Response**
```json
{
  "success": true,
  "data": null
}
```

---

## 3. 사업자 (Business)

### 3-1. 사업자 대시보드 조회

- **GET** `/api/business/dashboard?id={loginId}`

**Query Parameter**

| 파라미터 | 타입 | 설명 |
|----------|------|------|
| `id` | string | 로그인 아이디 |

**Response**
```json
{
  "success": true,
  "character": {
    "imageUrl": "string",
    "savedCount": 0
  },
  "data": {
    "today": {
      "date": "2026-06-15",
      "revenue": 0,
      "paymentCount": 0,
      "visitorCount": 0
    },
    "monthly": {
      "revenue": 0,
      "growthRate": 0.0
    },
    "stats": {
      "revisitRate": 0.0,
      "avgLikes": 0.0,
      "totalLikes": 0
    }
  }
}
```

> `growthRate` / `revisitRate` / `avgLikes`는 소수점 포함 `number` 타입입니다.
> 대시보드 응답은 구조상 최상위에 `character`와 `data`가 함께 위치합니다.

---

## 4. 사업자 Agent 분석 (Business Agent)

### 4-1. 사업 정보 Foundry Agent 분석

- **POST** `/api/business/analyze`

사업장명과 사업자주소를 Microsoft Foundry의 Test 에이전트로 전송하여 분석합니다.

**Request**
```json
{
  "businessName": "string",
  "businessAddress": "string"
}
```

**Response**
```json
{
  "businessName": "string",
  "businessAddress": "string",
  "agentResponse": "string",
  "success": true,
  "message": "string"
}
```

> `agentResponse`: Foundry Agent의 분석 결과
> `success`: 분석 성공 여부

---

## 부록

### 희귀도 코드표

| 값 | 의미 |
|----|------|
| `1` | 일반 (Common) |
| `2` | 레어 (Rare) |
| `3` | 전설 (Legendary) |

### 에러 응답 예시

> 현재 에러 처리는 미구현 상태로, 추후 공통 에러 포맷 확정 후 업데이트 예정입니다.
