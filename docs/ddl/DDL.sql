CREATE TABLE USERS (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    email      VARCHAR(100) NOT NULL UNIQUE COMMENT '로그인시 사용하는 이메일',
    name       VARCHAR(50)  NOT NULL COMMENT '이름',
    created_at TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '사용자 테이블';

CREATE TABLE POINT (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED NOT NULL UNIQUE COMMENT '사용자 아이디',
    balance    INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '사용자가 보유한 포인트',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '사용자 보유 포인트 테이블';

CREATE TABLE POINT_HISTORY (
    id             BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_id        BIGINT UNSIGNED NOT NULL COMMENT '사용자 아이디',
    before_balance INT UNSIGNED    NOT NULL COMMENT '변경 전 포인트 잔액',
    after_balance  INT UNSIGNED    NOT NULL COMMENT '변경 후 포인트 잔액',
    type           VARCHAR(20)     NOT NULL COMMENT '이력 타입(CHARGE / USE / REFUND',
    order_id       BIGINT UNSIGNED COMMENT '주문 아이디(이력 타입이 USE인 경우 주문 아이디가 존재한다.)',
    created_at     TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '사용자 보유 포인트 히스토리 테이블';

CREATE TABLE PRODUCT (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL COMMENT '상품명',
    price      INT UNSIGNED NOT NULL COMMENT '상품 가격(조회 성능을 위해 컬럼으로 추가했으며, product_price테이블에서 재고를 관리한다.',
    quantity   INT UNSIGNED NOT NULL COMMENT '상품 재고(조회 성능을 위해 컬럼으로 추가했으며, stock테이블에서 재고를 관리한다.)',
    status     VARCHAR(20)  NOT NULL COMMENT '상품 상태(조회 성능을 위해 컬럼으로 추가했으며, product_status테이블에서 재고를 관리한다.)',
    created_at TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '상품 테이블';

CREATE TABLE PRODUCT_STATUS (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT UNSIGNED NOT NULL UNIQUE COMMENT '상품 아이디',
    status     VARCHAR(20)     NOT NULL COMMENT '상품 상태(ON_SALE / STOP_SALE)',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '상품 판매상태 테이블';

CREATE TABLE PRODUCT_PRICE (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT UNSIGNED NOT NULL UNIQUE COMMENT '상품 아이디',
    price      INT UNSIGNED    NOT NULL COMMENT '상품 가격',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '상품 가격 테이블';

CREATE TABLE PRODUCT_PRICE_HISTORY (
    id           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    product_id   BIGINT UNSIGNED NOT NULL COMMENT '상품 아이디',
    before_price INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '변경 전 상품 가격',
    after_price  INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '변경 후 상품 가격',
    type         VARCHAR(20)     NOT NULL COMMENT '이력 타입(INFLATION: 물가상승 / DEFLATION: 물가하락 / CLEARANCE_SALE: 재고떨이 / SPECIAL_SALE: 특가 / BLACK_FRIDAY: 블랙프라이데이)',
    created_at   TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '상품 가격 히스토리 테이블';

CREATE TABLE STOCK (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT UNSIGNED NOT NULL UNIQUE COMMENT '상품 아이디',
    quantity   INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '상품 재고',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '상품 재고 테이블';

CREATE TABLE STOCK_HISTORY (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    product_id      BIGINT UNSIGNED NOT NULL COMMENT '상품 아이디',
    before_quantity INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '변경 전 상품 재고',
    after_quantity  INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '변경 후 상품 재고',
    type            VARCHAR(20)     NOT NULL COMMENT '이력 타입(RECEIVE: 입고 / RETURN: 반품 / ORDER: 주문 / CANCEL: 취소)',
    created_at      TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '상품 재고 히스토리 테이블';

CREATE TABLE CART_ITEM (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED NOT NULL COMMENT '사용자 아이디',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '상품 아이디',
    quantity   INT UNSIGNED    NOT NULL COMMENT '수량',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '장바구니 테이블';

CREATE TABLE ORDERS (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '사용자 아이디',
    amount          INT UNSIGNED    NOT NULL COMMENT '총 금액(sum(order_item.amount)',
    coupon_id       BIGINT UNSIGNED COMMENT '쿠폰 아이디',
    discount_amount INT UNSIGNED    NOT NULL COMMENT '주문에 적용된 할인 금액(coupon.target_type = ORDER)',
    total_amount    INT UNSIGNED    NOT NULL COMMENT '주문 총 금액(amount - discount_amount)',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ORDERED' COMMENT '주문 상태(ORDERED / PAYMENT / CANCELED / DELIVERY / COMPLETED)',
    created_at      TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at      TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at      TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '상품 주문 테이블';

CREATE TABLE ORDER_ITEM (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_id        BIGINT UNSIGNED NOT NULL,
    product_id      BIGINT UNSIGNED NOT NULL,
    product_name    VARCHAR(100)    NOT NULL COMMENT '주문 시점의 상품명',
    product_price   INT UNSIGNED    NOT NULL COMMENT '주문 시점의 상품 가격',
    quantity        INT UNSIGNED    NOT NULL,
    amount          INT UNSIGNED    NOT NULL COMMENT '주문 상품 금액(product_price * quantity)',
    coupon_id       BIGINT UNSIGNED COMMENT '사용한 쿠폰 아이디',
    discount_amount INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '상품별로 적용된 할인 금액(coupon.target_type = PRODUCT)',
    total_amount    INT UNSIGNED    NOT NULL COMMENT '총 상품 주문 금액(amount - discount_amount)',
    created_at      TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at      TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at      TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '상품 주문 상세 테이블';

CREATE TABLE ORDER_HISTORY (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_id   BIGINT UNSIGNED NOT NULL,
    status     VARCHAR(20)     NOT NULL DEFAULT 'ORDERED' COMMENT '주문 상태(ORDERED / PAYMENT / CANCELED / DELIVERY / COMPLETED)',
    user_id    BIGINT UNSIGNED NOT NULL COMMENT '사용자 아이디(주문 상태를 변경한 사용자의 아이디)',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '상품 주문 히스토리 테이블';

CREATE TABLE COUPON (
    id             BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(100)      NOT NULL COMMENT '쿠폰명',
    issue_limit    INT UNSIGNED      NOT NULL COMMENT '쿠폰 최대 발급 수량',
    quantity       INT UNSIGNED      NOT NULL COMMENT '발급 가능한 쿠폰 수량(조회 성능을 위해 컬럼으로 추가했으며, coupon_quantity테이블에서 수량을 관리한다.)',
    target_type    VARCHAR(20)       NOT NULL COMMENT '쿠폰 적용 대상(ORDER: 전체 주문 할인 / PRODUCT: 상품별 할인)',
    discount_type  VARCHAR(20)       NOT NULL COMMENT '할인 타입(FIXED: 정액 / RATE: 정률)',
    discount_value SMALLINT UNSIGNED NOT NULL COMMENT '할인 양(FIXED: 총 금액 - 명시한 값 / RATE: 총 금액 * (1 - 명시한 값/100)',
    max_discount   SMALLINT UNSIGNED NOT NULL COMMENT '최대 할인 금액',
    expired_type   VARCHAR(20)       NOT NULL COMMENT '유효기간 타입(FIXED: 고정 / PERIOD: 발급일자로 부터 계산)',
    expired_at     TIMESTAMP(0) COMMENT '쿠폰 만료일자 / 유효기간 타입이 FIXED인 경우 유효',
    expired_period SMALLINT UNSIGNED COMMENT '쿠폰 만료기간 / 발급일로 부터 명시된 일수를 더한 날까지 유효',
    created_at     TIMESTAMP(2)      NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at     TIMESTAMP(2)      NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at     TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '쿠폰 테이블';

CREATE TABLE COUPON_QUANTITY (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    coupon_id  BIGINT UNSIGNED NOT NULL COMMENT '쿠폰 아이디',
    quantity   INT UNSIGNED    NOT NULL COMMENT '발급 가능한 쿠폰 수량(최초에는 coupon.issue_limit와 동일하며 발급시마다 1씩 감소)',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '쿠폰 발급 수량 테이블';

CREATE TABLE COUPON_ISSUE (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    coupon_id  BIGINT UNSIGNED NOT NULL COMMENT '쿠폰 아이디',
    user_id    BIGINT UNSIGNED NOT NULL COMMENT '사용자 아이디',
    issued_at  TIMESTAMP(0)    NOT NULL COMMENT '쿠폰 발급일자',
    status     VARCHAR(20)     NOT NULL DEFAULT 'ISSUED' COMMENT '쿠폰 상태(ISSUED / USED)',
    expired_at TIMESTAMP(0)    NOT NULL COMMENT '쿠폰 유효기간(쿠폰의 유효기간 타입에 따라 다르게 계산)',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '발급된 쿠폰 테이블';

CREATE TABLE COUPON_USE_HISTORY (
    id        BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    coupon_id BIGINT UNSIGNED NOT NULL COMMENT '쿠폰 아이디',
    user_id   BIGINT UNSIGNED NOT NULL COMMENT '사용자 아이디',
    order_id  BIGINT UNSIGNED NOT NULL COMMENT '주문 아이디',
    used_at   TIMESTAMP(0)    NOT NULL COMMENT '쿠폰 사용일자'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '쿠폰 사용 히스토리 테이블';

CREATE TABLE BEST_PRODUCTS (
    id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    product_id    BIGINT UNSIGNED NOT NULL COMMENT '상품 아이디',
    total_sales   INT UNSIGNED    NOT NULL COMMENT '최근 N일간의 상품 총 판매량',
    total_amount  INT UNSIGNED    NOT NULL COMMENT '최근 N일간의 상품 총 판매 금액',
    statistics_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT '베스트 상품 통계 테이블';
