# IntelliJ AI Assistant로 커밋 메시지를 한국어로 생성하기

이 문서는 IntelliJ IDEA의 **AI Assistant**에서 “Generate Commit Message” 기능이 **한국어**로 메시지를 생성하도록 설정하는 방법과 팀 운영 팁을 정리합니다.

---

## 1) 적용 경로

1. `Settings(Preferences)` → **Tools → AI Assistant → Prompt Library**
2. 좌측 **Built-In Actions**에서 **Commit Message Generation** 선택
3. 우측 편집기에 아래 프롬프트(💬)를 **그대로 붙여 넣고** `Apply`

---

## 2) 커밋 메시지 생성 프롬프트 (💬 그대로 붙여 넣기)

```
요청사항:
- 항상 한국어로 커밋 메시지를 작성하세요.
- Conventional Commits 규칙을 따르되(type는 feat, fix, chore, docs, refactor, test, perf, style 등), 설명은 자연스러운 한국어로 작성합니다.
- 제목(요약)은 1줄로, 72자 이내의 명령형 문장으로 작성하고 마침표를 사용하지 않습니다.
- 본문은 3~5줄의 목록(불릿)으로 핵심만 간결하게 요약합니다.
- 불필요하게 긴 설명, 세부 구현 코드, 장황한 표현은 제외합니다.
- 고유명/기술명(API, Controller, Repository 등)은 영문 그대로 유지합니다.
- 브레이킹 체인지가 있으면 마지막 줄에 `BREAKING CHANGE: <내용>`을 추가합니다.
- 관련 이슈/티켓이 있으면 마지막 줄에 `Refs: <번호|URL>`을 추가합니다.

출력 형식:
- 1줄: `<type>: <짧고 명확한 한글 요약>`
- 1줄 공백
- 3~5줄 불릿 목록(각 줄은 간결한 한글 문장)
- (선택) 마지막 줄에 Refs, BREAKING CHANGE 표기

예시:
feat: 안테나 로그 API에 createdFrom, createdTo 필터 추가

- API 계층 및 Repository 쿼리에 날짜 필터 로직 추가
- Service/Controller에 createdFrom, createdTo 파라미터 반영
- OpenAPI 스펙에 신규 쿼리 파라미터 문서화
- JPA 쿼리 predicate로 날짜 조건 처리 보완

주의:
- Diff가 너무 작거나 의미가 불분명하면, 간결한 한국어로 이유를 설명하고 더 적절한 커밋 단위를 제안하세요.
- 결과에는 커밋 메시지 텍스트만 출력하세요(설명/서문/코드블록 금지).
```
---

## 3) 사용 방법(확인)

- **Commit** 창에서 메시지 입력란 우측 **AI Assistant 아이콘** → *Generate Commit Message with AI Assistant* 선택.

---

## 4) 팀/프로젝트 운영 팁

- 본 문서를 리포지토리 `docs/COMMIT_PROMPT_ko.md`로 보관하고, 온보딩 문서에 **설정 경로**와 **스크린샷**을 포함하세요.
- **짧은 버전(핵심 1–3 불릿)** 과 **자세 버전(4–6 불릿)** 두 가지 프롬프트를 Prompt Library에 별도 항목으로 저장해 상황별로 선택 사용하면 품질이 안정됩니다.
- 사내 용어·표기 일관성은 `docs/commit-style-guide.md`로 관리하세요. (예: “장비/디바이스”, “안테나/안테나장치”, 영문 약어 표기 원칙 등)

---

## 5) 선택 설정(권장)

- IDE 전역 AI 응답 언어 선호를 한국어로 지정하려면  
  `Settings → Tools → AI Assistant → (Natural Language)`에서 **Korean** 선택.  
  > *커밋 메시지의 언어 고정은 위 프롬프트로 강제하는 것이 가장 확실합니다.*

---
