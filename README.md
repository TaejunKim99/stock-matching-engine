# Stock Matching Engine (VeloTrade)
고성능 Redis 기반의 실시간 주식 주문 체결 시스템 > 단순 CRUD를 넘어, 금융 데이터의 정합성과 동시성 제어, 그리고 실시간 전파를 해결하는 데 초점을 맞춘 프로젝트입니다.

## 1. 프로젝트 목표
이 프로젝트는 실제 증권 거래소의 핵심 메커니즘을 모사하여 다음 기술적 난제들을 해결합니다.

Price-Time Priority: 가격 우선, 시간 우선 원칙에 따른 정확한 매칭 엔진 구현

Concurrency Control: 동일 종목에 몰리는 동시 주문에 대한 레이스 컨디션 방지

Data Integrity: 금융 거래의 핵심인 자산 및 주문 데이터의 ACID 정합성 보장

Real-time Streaming: 체결 결과를 0.x초 내에 사용자에게 실시간으로 전파

##  2. Tech Stack
Backend
Language: Java 17 (LTS)

Framework: Spring Boot 3.x

Database: PostgreSQL (Order/Trade Ledger)

In-Memory: Redis (Orderbook / Distributed Lock)

Messaging: Apache Kafka (Event-driven Architecture)

Frontend
Framework: Vue.js 3 + TypeScript

Library: TradingView Lightweight Charts (시세 차트)

## 3. System Architecture
본 시스템은 CQRS(명령 및 조회 책임 분리) 패턴과 이벤트 기반 설계를 지향합니다.

주문 접수: API 서버가 주문을 검증하고 예수금을 홀딩(Locking)합니다.

이벤트 발행: 주문 요청을 Kafka로 전송하여 매칭 엔진과의 결합도를 낮춥니다.

체결 엔진: Redis ZSET을 활용하여 가격-시간 우선순위로 매칭을 수행합니다.

실시간 푸시: 체결 즉시 WebSocket(STOMP)을 통해 클라이언트에 데이터를 전파합니다.

##  4. 핵심 설계 포인트
✅ Redis ZSET을 활용한 우선순위 큐 설계
단순 가격 정렬의 한계를 극복하기 위해 Score 조합 방식을 사용했습니다.

Score 공식: Price + (Timestamp / 10^12)

동일 가격 내에서도 시간 순서를 100% 보장하는 정렬 구조를 설계했습니다.

✅ Redisson 분산 락을 통한 동시성 제어
종목 코드별(LOCK:{symbol})로 분산 락을 획득하여, 동일 종목에 대한 매칭 정합성을 보장합니다.

✅ 가용 잔고(Available) vs 주문 중 잔고(Locked)
중복 주문 방지 및 자산 보호를 위해 주문 시점에 자산을 즉시 분리 관리하는 홀딩 로직을 구현했습니다.

## 5. 시작하기 (Quick Start)
본 프로젝트는 Docker Compose를 통해 인프라 환경을 1분 만에 구축할 수 있습니다.

Bash
### 1. 저장소 복제
git clone https://github.com/your-id/stock-matching-engine.git

### 2. 인프라 실행 (PostgreSQL, Redis)
docker-compose up -d

### 3. 백엔드 실행
cd backend
./gradlew bootRun

## 6. License
이 프로젝트는 MIT 라이선스를 따릅니다.