# API First & TDD 기반 API 서버

이 프로젝트는 **API First** 접근 방식과 **테스트 주도 개발(TDD)** 원칙을 따라 개발된 API 서버입니다.

## 1. 프로젝트 개요

API First 원칙에 따라, 실제 코드를 작성하기 전에 API 명세를 먼저 설계합니다. `openapi.yml`에 정의된 명세를 기반으로 **OpenAPI Generator**를 사용하여 API 인터페이스와 DTO를 자동으로 생성합니다.

생성된 인터페이스를 실제 비즈니스 로직과 연결하기 위해 **Delegate Pattern**을 사용합니다. 이를 통해 API 명세와 구현 코드를 분리하여 유지보수성을 높입니다.

또한, TDD를 통해 모든 기능은 테스트 코드를 먼저 작성하고 이를 통과시키는 방식으로 구현됩니다. 이는 코드의 안정성을 높이고, 유지보수를 용이하게 만듭니다.

## 2. 프로젝트 구조

```
.
├── @docs/                  # 1. 프로젝트 관련 주요 결정사항 및 가이드 문서
│   └── openapi-generator-setup.md
├── build/                    # 2. 빌드 결과물이 생성되는 폴더
│   └── generated/            #    (OpenAPI Generator가 생성한 코드가 여기에 위치)
├── openapi/                  # 3. API 명세(Specification) 파일
│   └── openapi.yml
└── src/
    ├── main/
    │   ├── kotlin/
    │   │   └── com/example/sampleapiupload/
    │   │       ├── delegate/     # 4. API 비즈니스 로직 구현체 (Delegate Impl)
    │   │       └── SampleApiUploadApplication.kt
    │   └── resources/
    │       └── application.yml
    └── test/                   # 5. 테스트 코드
```

1.  **`@docs/`**: 프로젝트의 아키텍처, 설정, 주요 결정 사항 등 개발에 필요한 가이드 문서를 보관합니다.
2.  **`build/generated/`**: OpenAPI Generator에 의해 자동으로 생성된 코드(`...Api.kt`, `...ApiDelegate.kt`, 모델 클래스 등)가 위치하는 곳입니다. **이 폴더의 내용은 직접 수정하지 않습니다.**
3.  **`openapi/`**: API의 '설계도'인 `openapi.yml` 파일이 위치합니다. 모든 API 변경은 이 파일에서부터 시작됩니다.
4.  **`src/main/.../delegate/`**: 자동 생성된 `...ApiDelegate` 인터페이스를 상속받아 실제 비즈니스 로직을 구현하는 클래스들이 위치합니다. **우리가 직접 작성하는 대부분의 비즈니스 코드가 여기에 해당됩니다.**
5.  **`src/test/`**: 단위 테스트, 통합 테스트 등 모든 테스트 코드가 위치합니다.

## 3. 기술 스택

- **언어**: Kotlin
- **프레임워크**: Spring Boot
- **빌드 도구**: Gradle
- **API 명세**: OpenAPI 3.0
- **API 코드 생성**: OpenAPI Generator
- **테스트**: JUnit 5, MockK

## 4. 개발 프로세스

1.  **API 설계**: `openapi/openapi.yml` 파일에 API 명세를 정의하거나 수정합니다.
2.  **빌드 및 코드 생성**: `./gradlew build`를 실행하여 API 인터페이스와 DTO를 자동으로 생성하고 프로젝트를 빌드합니다.
3.  **구현 (TDD)**: 생성된 `...ApiDelegate` 인터페이스를 구현하는 클래스를 `delegate` 패키지 안에 만들고, TDD 사이클에 따라 비즈니스 로직을 개발합니다.

## 5. 실행 방법

### 사전 준비

- JDK 17 이상
- Gradle

### 설치 및 실행

1.  **프로젝트 클론**
    ```bash
    git clone {저장소_URL}
    cd sample-api-upload
    ```

2.  **빌드 및 코드 생성**
    ```bash
    ./gradlew build
    ```

3.  **애플리케이션 실행**
    ```bash
    ./gradlew bootRun
    ```
    서버가 `http://localhost:8080`에서 실행됩니다.

## 6. 테스트 (TDD)

모든 테스트 코드는 `src/test` 디렉토리 내에 위치합니다. 아래 명령어로 전체 테스트를 실행할 수 있습니다.

```bash
./gradlew test
```
