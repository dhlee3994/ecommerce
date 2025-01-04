ALTER TABLE POINT
    ADD CONSTRAINT fk_point_user FOREIGN KEY (user_id) REFERENCES USERS (id);

ALTER TABLE POINT_HISTORY
    ADD CONSTRAINT fk_point_history_user FOREIGN KEY (user_id) REFERENCES USERS (id),
    ADD CONSTRAINT fk_point_history_order FOREIGN KEY (order_id) REFERENCES ORDERS(id);

ALTER TABLE PRODUCT_STATUS
    ADD CONSTRAINT fk_product_status_product_id FOREIGN KEY (product_id) REFERENCES PRODUCT (id);

ALTER TABLE PRODUCT_PRICE
    ADD CONSTRAINT fk_product_price_product FOREIGN KEY (product_id) REFERENCES PRODUCT (id);

ALTER TABLE PRODUCT_PRICE_HISTORY
    ADD CONSTRAINT fk_product_price_history_product FOREIGN KEY (product_id) REFERENCES PRODUCT (id);

ALTER TABLE STOCK
    ADD CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES PRODUCT (id);

ALTER TABLE STOCK_HISTORY
    ADD CONSTRAINT fk_stock_history_product FOREIGN KEY (product_id) REFERENCES PRODUCT (id);

ALTER TABLE CART_ITEM
    ADD CONSTRAINT fk_cart_item_user FOREIGN KEY (user_id) REFERENCES USERS (id),
    ADD CONSTRAINT fk_cart_item_product FOREIGN KEY (product_id) REFERENCES PRODUCT(id);

ALTER TABLE ORDERS
    ADD CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES USERS (id),
    ADD CONSTRAINT fk_orders_coupon FOREIGN KEY (coupon_id) REFERENCES COUPON(id);

ALTER TABLE ORDER_ITEM
    ADD CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES ORDERS (id),
    ADD CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES PRODUCT(id),
    ADD CONSTRAINT fk_order_item_coupon FOREIGN KEY (coupon_id) REFERENCES COUPON(id);

ALTER TABLE ORDER_HISTORY
    ADD CONSTRAINT fk_order_history_order FOREIGN KEY (order_id) REFERENCES ORDERS (id),
    ADD CONSTRAINT fk_order_history_user FOREIGN KEY (user_id) REFERENCES USERS(id);

ALTER TABLE COUPON_QUANTITY
    ADD CONSTRAINT fk_coupon_quantity_coupon FOREIGN KEY (coupon_id) REFERENCES COUPON (id);

ALTER TABLE COUPON_ISSUE
    ADD CONSTRAINT fk_coupon_issue_coupon FOREIGN KEY (coupon_id) REFERENCES COUPON (id),
    ADD CONSTRAINT fk_coupon_issue_user FOREIGN KEY (user_id) REFERENCES USERS(id);

ALTER TABLE COUPON_USE_HISTORY
    ADD CONSTRAINT fk_coupon_use_history_coupon FOREIGN KEY (coupon_id) REFERENCES COUPON (id),
    ADD CONSTRAINT fk_coupon_use_history_user FOREIGN KEY (user_id) REFERENCES USERS(id),
    ADD CONSTRAINT fk_coupon_use_history_order FOREIGN KEY (order_id) REFERENCES ORDERS(id);

ALTER TABLE BEST_PRODUCTS
    ADD CONSTRAINT fk_best_products_product_id FOREIGN KEY (product_id) REFERENCES PRODUCT (id);
