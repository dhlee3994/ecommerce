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

