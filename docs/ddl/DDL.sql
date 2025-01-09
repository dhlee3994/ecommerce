CREATE TABLE USERS (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(50)  NOT NULL UNIQUE COMMENT '이름',
    created_at TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) COMMENT '사용자 테이블';

CREATE TABLE POINT (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED NOT NULL UNIQUE COMMENT '사용자 아이디',
    point      INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '사용자가 보유한 포인트',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) COMMENT '사용자 보유 포인트 테이블';

CREATE TABLE PRODUCT (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL COMMENT '상품명',
    price      INT UNSIGNED NOT NULL COMMENT '상품 가격',
    quantity   INT UNSIGNED NOT NULL COMMENT '상품 재고(조회 성능을 위해 컬럼으로 추가했으며, stock테이블에서 재고를 관리한다.)',
    created_at TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) COMMENT '상품 테이블';

CREATE TABLE STOCK (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT UNSIGNED NOT NULL UNIQUE COMMENT '상품 아이디',
    quantity   INT UNSIGNED    NOT NULL DEFAULT 0 COMMENT '상품 재고',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) COMMENT '상품 재고 테이블';

CREATE TABLE ORDERS (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED NOT NULL COMMENT '사용자 아이디',
    amount     INT UNSIGNED    NOT NULL COMMENT '주문 총 금액',
    status     VARCHAR(20)     NOT NULL DEFAULT 'ORDERED' COMMENT '주문 상태(ORDERED / PAID)',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) COMMENT '상품 주문 테이블';

CREATE TABLE ORDER_ITEM (
    id           BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_id     BIGINT UNSIGNED NOT NULL,
    product_id   BIGINT UNSIGNED NOT NULL,
    product_name VARCHAR(100)    NOT NULL COMMENT '주문 시점의 상품명',
    price        INT UNSIGNED    NOT NULL COMMENT '주문 시점의 상품 가격',
    quantity     INT UNSIGNED    NOT NULL COMMENT '주문 수량',
    created_at   TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at   TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at   TIMESTAMP(2)
) COMMENT '상품 주문 상세 테이블';

CREATE TABLE PAYMENT (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    order_id   BIGINT UNSIGNED NOT NULL,
    amount     INT UNSIGNED    NOT NULL COMMENT '결제 금액',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) COMMENT '결제 테이블';

CREATE TABLE COUPON (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100)      NOT NULL COMMENT '쿠폰명',
    issue_limit     INT UNSIGNED      NOT NULL COMMENT '쿠폰 최대 발급 수량',
    quantity        INT UNSIGNED      NOT NULL COMMENT '발급 가능한 쿠폰 수량(조회 성능을 위해 컬럼으로 추가했으며, coupon_quantity테이블에서 수량을 관리한다.)',
    discount_amount SMALLINT UNSIGNED NOT NULL COMMENT '할인 금액',
    expired_at      TIMESTAMP(2)      NOT NULL COMMENT '쿠폰 만료일',
    created_at      TIMESTAMP(2)      NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at      TIMESTAMP(2)      NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at      TIMESTAMP(2)
) COMMENT '쿠폰 테이블';

CREATE TABLE COUPON_QUANTITY (
    id         BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    coupon_id  BIGINT UNSIGNED NOT NULL COMMENT '쿠폰 아이디',
    quantity   INT UNSIGNED    NOT NULL COMMENT '발급 가능한 쿠폰 수량(최초에는 coupon.issue_limit와 동일하며 발급시마다 1씩 감소)',
    created_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at TIMESTAMP(2)    NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    deleted_at TIMESTAMP(2)
) COMMENT '쿠폰 발급 수량 테이블';

CREATE TABLE ISSUED_COUPON (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    coupon_id       BIGINT UNSIGNED   NOT NULL COMMENT '쿠폰 아이디',
    user_id         BIGINT UNSIGNED   NOT NULL COMMENT '사용자 아이디',
    discount_amount SMALLINT UNSIGNED NOT NULL COMMENT '할인 금액',
    issued_at       TIMESTAMP(2)      NOT NULL DEFAULT CURRENT_TIMESTAMP(2) COMMENT '쿠폰 발급일',
    expired_at      TIMESTAMP(2)      NOT NULL COMMENT '쿠폰 만료일',
    used_at         TIMESTAMP(2) COMMENT '쿠폰 사용일',
    deleted_at      TIMESTAMP(2)
) COMMENT '발급된 쿠폰 테이블';

CREATE TABLE COUPON_USE_HISTORY (
    id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    issued_coupon_id BIGINT UNSIGNED NOT NULL COMMENT '발급된 쿠폰 아이디',
    order_id         BIGINT UNSIGNED NOT NULL COMMENT '주문 아이디',
    used_at          TIMESTAMP(0)    NOT NULL COMMENT '쿠폰 사용일자'
) COMMENT '쿠폰 사용 히스토리 테이블';
