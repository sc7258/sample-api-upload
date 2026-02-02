# OpenAPI Generator 설정 가이드

이 문서는 우리 프로젝트의 API First 개발 워크플로우의 핵심인 **OpenAPI Generator**의 설정과 동작 방식, 그리고 주요 결정 사항을 정리한 최종 가이드입니다.

## 1. 목표

- **API First**: `openapi/openapi.yml`에 정의된 명세를 기반으로 API 관련 코드를 자동으로 생성합니다.
- **관심사 분리**: 자동 생성 코드와 실제 비즈니스 로직 코드를 명확하게 분리하여 유지보수성을 극대화합니다.
- **자동화**: `build` 시 코드 생성이 자동으로 실행되도록 하여 개발자의 실수를 방지합니다.

## 2. `build.gradle` 핵심 설정

`build.gradle` 파일에 정의된 `openApiGenerate` 태스크는 다음과 같이 설정되어 있습니다.

```groovy
tasks.named('openApiGenerate') {
    generatorName = "kotlin-spring"
    inputSpec = "$projectDir/openapi/openapi.yml"
    outputDir = "$buildDir/generated"
    apiPackage = "com.example.sampleapiupload.api"
    modelPackage = "com.example.sampleapiupload.model"
    configOptions = [
        "delegatePattern": "true",
        "useSpringBoot3": "true",
        "documentationProvider": "springdoc",
        "interfaceOnly": "true",
        "useTags": "true" // ★★★ 가장 중요한 옵션
    ]
}
```

### `configOptions` 상세 설명

- `delegatePattern: "true"`: API의 실제 구현 로직을 `...ApiDelegate` 인터페이스를 통해 위임하는 Delegate 패턴을 사용합니다. 이를 통해 자동 생성 코드와 비즈니스 로직을 분리합니다.
- `interfaceOnly: "true"`: 실제 로직이 들어가는 구현 클래스 없이, 순수한 `...ApiDelegate` 인터페이스만 생성하도록 합니다.
- `useSpringBoot3: "true"`: Spring Boot 3.x 버전에 맞는 코드를 생성합니다.
- `useTags: "true"`: **(핵심)** API를 그룹화하는 기준인 `tags`를 기반으로 API 인터페이스 파일을 분리하여 생성합니다.

## 3. 태그(Tag) 기반 API 분리 및 이름 생성 규칙

`useTags: "true"` 옵션은 `openapi.yml`에 정의된 `tags`를 기준으로 생성될 파일의 이름을 결정합니다.

### ★ 이름 생성 규칙 (실수를 반복하지 않기 위한 핵심)

OpenAPI Generator는 태그 이름을 다음과 같은 규칙에 따라 클래스 이름으로 변환합니다.

1.  태그 문자열에서 공백이나 특수문자를 제거합니다.
2.  각 단어의 첫 글자를 대문자로 바꾸는 **CamelCase**로 변환합니다.
3.  변환된 이름 뒤에 `Api`, `ApiDelegate` 등의 접미사를 붙입니다.

**예시:**
- `tags: ["hello example"]` → `HelloExample`으로 변환 → `HelloExampleApiDelegate.kt` 생성
- `tags: ["user-management"]` → `UserManagement`으로 변환 → `UserManagementApiDelegate.kt` 생성

### 모범 사례 (Best Practice)

이러한 변환 규칙으로 인한 혼란을 피하기 위해, `openapi.yml`의 `tags`를 처음부터 클래스 이름처럼 작성하는 것을 권장합니다.

```yaml
# 나쁜 예 👎 (변환 결과를 예측해야 함)
tags:
  - name: hello example

# 좋은 예 👍 (생성될 파일 이름이 명확하게 예측됨)
tags:
  - name: HelloExample
```

## 4. 빌드 시 자동 코드 생성

`build.gradle` 파일 하단에는 다음 규칙이 추가되어 있습니다.

```groovy
tasks.named("compileKotlin").configure {
    dependsOn("openApiGenerate")
}
```

이 설정은 `./gradlew build` 또는 IDE의 빌드 버튼을 누를 때, Kotlin 코드를 컴파일하기 전에 항상 `openApiGenerate` 태스크를 먼저 실행하도록 보장합니다. 이를 통해 개발자가 코드 생성을 잊어버리는 실수를 원천적으로 방지합니다.

---

이 문서를 통해 다시는 설정 문제로 시간을 낭비하는 일이 없기를 바랍니다.
