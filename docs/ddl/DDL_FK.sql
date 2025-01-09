-- POINT 테이블에 FK 추가
ALTER TABLE POINT
    ADD CONSTRAINT fk_point_user
        FOREIGN KEY (user_id) REFERENCES USERS (id);

-- STOCK 테이블에 FK 추가
ALTER TABLE STOCK
    ADD CONSTRAINT fk_stock_product
        FOREIGN KEY (product_id) REFERENCES PRODUCT (id);

-- ORDERS 테이블에 FK 추가
ALTER TABLE ORDERS
    ADD CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES USERS (id);

-- ORDER_ITEM 테이블에 FK 추가
ALTER TABLE ORDER_ITEM
    ADD CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES ORDERS (id),
    ADD CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id) REFERENCES PRODUCT (id);

-- PAYMENT 테이블에 FK 추가
ALTER TABLE PAYMENT
    ADD CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id) REFERENCES ORDERS (id);

-- COUPON_QUANTITY 테이블에 FK 추가
ALTER TABLE COUPON_QUANTITY
    ADD CONSTRAINT fk_coupon_quantity_coupon
        FOREIGN KEY (coupon_id) REFERENCES COUPON (id);

-- ISSUED_COUPON 테이블에 FK 추가
ALTER TABLE ISSUED_COUPON
    ADD CONSTRAINT fk_issued_coupon_coupon
        FOREIGN KEY (coupon_id) REFERENCES COUPON (id),
    ADD CONSTRAINT fk_issued_coupon_user
        FOREIGN KEY (user_id) REFERENCES USERS (id);

-- COUPON_USE_HISTORY 테이블에 FK 추가
ALTER TABLE COUPON_USE_HISTORY
    ADD CONSTRAINT fk_coupon_use_history_issued_coupon
        FOREIGN KEY (issued_coupon_id) REFERENCES ISSUED_COUPON (id),
    ADD CONSTRAINT fk_coupon_use_history_order
        FOREIGN KEY (order_id) REFERENCES ORDERS (id);
