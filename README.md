# 이커머스

- 간단한 이커머스 서비스
- 사용 기술: JDK 17, Spring Boot 3.4.1, MySQL, JPA

## 시나리오 분석

<details>
<summary>접기/펼치기</summary>

- 장바구니 API는 API 스펙에서 제외되었지만, '사용자 식별자와 (상품 ID, 수량) **목록**을 입력받아'라는 주문/결제 API 요구사항에 따라 추가한다. 

#### 잔액 충전/조회 API

- 잔액 충전/조회는 회원만 가능하다.
- 최소 충전 포인트는 1이상이다.
- 최대 충전 포인트이상으로 포인트를 충전할 수 없다. 단, 상품 환불로 돌려 받은 포인트는 최대 충전 포인트 이상으로 보유할 수 있다.

#### 상품 조회 API

- 상품 목록, 상세 조회는 비회원/회원 모두 가능하다.
- 상품의 가격, 수량은 변동이 심하다고 가정한다.
- 상품의 그룹, 카테고리는 없다고 가정한다.
- 비회원/회원 모두 상품 상세 조회에서 수량 선택 후 장바구니에 추가할 수 있다.
- 상품 상세 조회에서 수량 선택 후 바로 구매가 가능하다. 단, 구매는 회원만 가능하다.

#### 선착순 쿠폰 API
 
- 쿠폰 목록, 상세 조회는 비회원/회원 모두 가능하다.
- 쿠폰 발급은 회원만 가능하다.
- 쿠폰마다 최대 발급 가능 갯수가 정해져있다.
- 쿠폰은 상품에 적용할 수 있는 타입과 주문 전체에 적용할 수 있는 타입이 있다.
- 쿠폰의 할인 방식은 정액(정해진 금액 할인)할인과 정률(정해진 비율 할인)할인이 있다.
- 쿠폰마다 최대 할인 금액이 존재한다.
- 실제 할인 금액은 할인을 적용한 금액과 최대 할인 금액 중 낮은 금액을 할인한다.
- 쿠폰의 유효 기간은 유효 날짜가 지정되는 방식(e.g. 2025년 1월 10일까지)과 발급 후 몇일 이내까지 유효한 방식(e.g. 발급 후 10일동안 유효)이 있다.
- 동일한 회원에 대해 중복 쿠폰 발급은 되지 않는다. 쿠폰은 1인 1매를 원칙으로 한다.

#### 주문/결제 API

- 주문은 상품 상세 페이지에서 수량 선택 후 바로 주문하는 방법과 장바구니에서 여러 상품을 주문하는 방법이 있다.
- 주문 시점의 판매 상태, 가격, 재고로 주문이 이루어진다.
  - 주문 시점에 판매 상태가 판매 중이 아닌 경우 주문할 수 없다.
  - 주문 시점의 가격으로 주문 금액이 계산된다.
  - 주문 시점의 재고가 사용자가 요청한 수량보다 적으면 주문할 수 없다.
- 쿠폰으로 할인된 금액을 실제로 결제한다.
- 이미 충전된 포인트로 결제하며, 부족한 경우 결제할 수 없다. 포인트 충전 후 다시 결제를 시도할 수 있으며 그 동안 변경된 가격/재고의 영향을 받지 않는다.
- 결제 성공시 주문 정보를 데이터 플랫폼에 전송하며, 전송 실패시 처리는 추후 정한다.

#### 상위 상품 조회 API

- 최근 3일간 가장 많이 팔린 상위 5개의 상품 정보를 제공한다.
- 최근 3일이란 정각(00시 00분)을 기준으로 한다.
  - e.g. 2025년 1월 4일에 집계하는 상위 상품은 2025년 1월 1일 00시 00분부터 2025년 1월 4분 00시 00분까지의 판매 결과를 집계한다.
- 매일 00시 00분에 스케줄러를 통해 상위 상품을 집계한다.
- 스케줄러에서 집계 실패시 재시도하지 않고, 모니터링 툴을 통해 개발자에게 알린다.

#### 장바구니 API

- 상품 상세 페이지에서 판매 중이며, 재고가 충분하다면 장바구니에 등록할 수 있다.
- 장바구니 API는 비회원/회원 모두 이용할 수 있다.
- 장바구니에서 제품의 수량 변경시 보유 재고이하로 설정해야한다.
- 장바구니에 등록한 제품의 판매 상태, 가격, 재고는 언제든지 변경될 수 있다.
- 장바구니에 등록한 상품들 중 유효한 판매 상태, 재고를 가진 상품들만 주문할 수 있다.

</details>

## Mock API 서버 구동

1. docker-compose.yml 실행
2. `http://localhost:8082` 접속

## 스웨거 스크린샷

<details>
<summary>접기/펼치기</summary>

![swagger.JPG](./docs/swagger.JPG)

</details>

## 프로젝트 구조

<details>
<summary>접기/펼치기</summary>

```markdown
main
├── java
│ └── io
│     └── hhplus
│         └── ecommerce
│             ├── EcommerceApplication.java
│             ├── cart
│             │ ├── application
│             │ │ ├── request
│             │ │ └── response
│             │ ├── domain
│             │ ├── infra
│             │ │ ├── request
│             │ │ └── response
│             │ └── presentation
│             │     ├── request
│             │     └── response
│             ├── coupon
│             │ ├── application
│             │ │ ├── request
│             │ │ └── response
│             │ ├── domain
│             │ ├── infra
│             │ │ ├── request
│             │ │ └── response
│             │ └── presentation
│             │     ├── request
│             │     └── resonse
│             ├── global
│             │ ├── CommonApiResponse.java
│             │ ├── config
│             │ └── exception
│             │ └── openapi
│             ├── order
│             │ ├── application
│             │ │ ├── request
│             │ │ └── response
│             │ ├── domain
│             │ ├── infra
│             │ │ ├── request
│             │ │ └── response
│             │ └── presentation
│             │     ├── request
│             │     └── response
│             ├── payment
│             │ ├── application
│             │ │ ├── request
│             │ │ └── response
│             │ ├── domain
│             │ ├── infra
│             │ │ ├── request
│             │ │ └── response
│             │ └── presentation
│             │     ├── request
│             │     └── response
│             ├── point
│             │ ├── application
│             │ │ ├── request
│             │ │ └── response
│             │ ├── domain
│             │ ├── infra
│             │ │ ├── request
│             │ │ └── response
│             │ └── presentation
│             │     ├── request
│             │     └── response
│             └── product
│                 ├── application
│                 │ ├── request
│                 │ └── response
│                 ├── domain
│                 ├── infra
│                 │ ├── request
│                 │ └── response
│                 └── presentation
│                     ├── request
│                     └── response
└── resources
    └── application.yml

```

</details>

## 회고

<details>
<summary>접기/펼치기</summary>

챕터 2를 마무리하며

2024년 12월 28일부터 오늘(2025년 1월 17일)까지 3주 동안 이커머스 도메인을 설계부터 
간단한 구현까지 진행하면서 각 주차 별로 겪은 일을 돌아보고 마지막에 느낀 점을 작성하려고 한다.

첫 주차는 설계 주차였다. 이커머스라는 생소한 도메인이었지만 그동안 기획자 없이 고객과 
직접 소통하면서 시스템을 만들어왔었기 때문에 크게 걱정하지 않았다. 하지만 돌이켜보면 
첫 주차가 가장 힘들었던 시간이었다. 중간에 공휴일이 껴있었음에도 과제 마지막 날에 밤샘을 피할 수 없었다.
시퀀스 다이어그램과 플로우 차트를 처음 작성해 보는 것은 크게 문제가 되지 않았다. 설계 그 자체가 문제였다. 
'카테고리별로 검색 조건을 다르게 하려면 어떻게 해야 할까?', '상품의 상세 옵션은 어떻게 구현하는 거지?' 등의 
고민을 하다 보니 3일 차부터는 꿈에서도 설계하기 시작했다.

둘째 주차에는 1주 차에 했던 설계를 바탕으로 기능 구현을 진행했다. API 하나를 구현할 때마다 바로바로 테스트 코드를 작성했고,
가능하다면 통합 테스트까지 작성했다. 하지만 점점 여러 도메인간의 협력이 필요한 주문, 결제와 같은 비즈니스 문제를 풀어나가면서 
처음에 작성했던 API 명세들이 점점 변경되기 시작했고, 테스트 코드도 깨지기 시작했다. 
뭐, 덕분에 테스트 코드를 더 많이 짜볼 수 있어서 좋았지만, 실제 회사에서 일어난 일이었다고 생각하면 아찔했다.

셋째 주차는 2주 차에 구현한 기능들을 보완했다. 설계하면서 '정액/정률 쿠폰' 기능 구현을 가장 기대했었는데 
시간 관계상 해당 기능을 구현하지 못했다. 그래서 금요일부터 바로 기능 구현에 들어갔다. 역시나 기대했던 만큼 생각할 거리가 많았던 기능이었다. 
다만 3주 차 과제와 직접적인 관련은 없기 때문에 과제 PR에 포함하지 못하는 게 아쉬웠다.
그리고 CORS 필터를 구현했다. 기능을 구현하자마자 테스트를 할 수 있을까라는 생각부터 들어서 조금은 성장한 것 같은 기분이 들었다.

5주 차 멘토링 때에도 잠깐 얘기했었는데 그동안 공부를 한다고 하면 책이나 인강을 보고 따라 치는 정도가 전부였다.
뭔가를 만들어보고 싶어도 내가 짜는 코드가 좋은 코드인지 확신이 서지 않은 상태에서 무작정 만들기만 하는 것은  
지도 없이 망망대해를 건너는 것과 같다고 생각했다. 
하지만 이번 과제를 진행하면서 어떤 고민을 해야 좀 더 좋은 코드가 될 수 있을지 조금은 깨달은 것 같다.   

</details>
