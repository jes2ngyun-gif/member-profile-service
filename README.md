# Member Profile Service

## 프로젝트 소개

Spring Boot와 AWS를 활용하여 팀원 정보를 관리하고 프로필 이미지를 업로드하는 백엔드 서비스를 구현하는 프로젝트입니다.

## 프로젝트 목표

- 팀원 정보 등록 및 조회 API 구현
- AWS 기반 인프라 구축
- Stateless 아키텍처 구성
- EC2, RDS, S3를 활용한 서비스 운영
- Docker 및 CI/CD 적용

## 기술 스택

- Java 17
- Spring Boot 4.0.7
- Spring Web
- Spring Data JPA
- Validation
- H2 Database
- MySQL
- Spring Boot Actuator
- AWS

---

# LV0. AWS Budget 설정

## 구현 내용

AWS Budgets를 사용하여 월 예산을 100 USD로 설정하고, 실제 비용이 예산의 80%를 초과하면 이메일 알림을 받을 수 있도록 구성하였다.

### 스크린샷

![LV0 Budget](docs/images/lv0-budget.png)

## 핵심 개념

AWS Budget은 사용한 비용을 기준으로 예산을 모니터링하고, 사용자가 설정한 임계값에 도달하면 이메일 등의 방법으로 알림을 제공하는 비용 관리 서비스이다.

## 왜 Budget을 먼저 설정하는가?

AWS는 사용량 기반 과금(Pay-as-you-go) 방식이다.

EC2, RDS, S3와 같은 리소스를 실수로 계속 실행하면 예상보다 많은 비용이 발생할 수 있다.

Budget을 먼저 설정하면 일정 금액에 도달했을 때 즉시 알림을 받아 비용을 관리할 수 있다.

## 처리 흐름

사용량 증가

↓

AWS Budget 비용 모니터링

↓

80% 도달

↓

이메일 알림 발송

↓

리소스 점검 및 비용 관리

---

# LV1. 네트워크 구축 및 애플리케이션 배포

(작성 예정)

---

# LV2. RDS 및 Parameter Store

(작성 예정)

---

# LV3. S3 프로필 이미지

(작성 예정)

---

# LV4. Docker & CI/CD

(작성 예정)

---

# LV5. ALB & HTTPS

(작성 예정)

---

# LV6. CloudFront

(작성 예정)