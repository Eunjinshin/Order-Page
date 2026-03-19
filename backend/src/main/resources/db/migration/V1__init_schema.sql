-- ============================================================
-- Odersite — 초기 스키마 정의 (ERD v1.0 기준)
-- 작성일: 2026-03-19
-- 테이블 수: 23개
-- ============================================================

-- ───────────────────────────────
-- 1. 회원·인증 도메인
-- ───────────────────────────────

CREATE TABLE MEMBER_USER (
    user_id     INT             NOT NULL AUTO_INCREMENT,
    nickname    VARCHAR(50)     NOT NULL,
    user_admin  BOOLEAN         NOT NULL DEFAULT FALSE,
    PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE AUTH_LOGIN (
    user_id             INT             NOT NULL,
    email               VARCHAR(255)    NOT NULL,
    password_hash       VARCHAR(255)    NULL,
    refresh_token       VARCHAR(512)    NOT NULL,
    token_expires_at    DATETIME        NULL,
    login_type          ENUM('EMAIL', 'GOOGLE') NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at          DATETIME        NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_auth_email (email),
    CONSTRAINT fk_auth_login_user FOREIGN KEY (user_id) REFERENCES MEMBER_USER (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE MEMBER_PROFILE (
    profile_id  INT             NOT NULL AUTO_INCREMENT,
    user_id     INT             NOT NULL,
    user_name   VARCHAR(100)    NULL,
    user_phone  VARCHAR(20)     NULL,
    PRIMARY KEY (profile_id),
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES MEMBER_USER (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE MEMBER_ADDRESS (
    address_id      INT             NOT NULL AUTO_INCREMENT,
    user_id         INT             NOT NULL,
    address_name    VARCHAR(50)     NULL,
    address         VARCHAR(255)    NULL,
    zip_code        VARCHAR(10)     NULL,
    is_default      BOOLEAN         NOT NULL DEFAULT FALSE,
    detail_address  VARCHAR(255)    NULL,
    PRIMARY KEY (address_id),
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES MEMBER_USER (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────
-- 2. 상품 도메인
-- ───────────────────────────────

CREATE TABLE SHOP (
    shop_id     INT             NOT NULL AUTO_INCREMENT,
    shop_name   VARCHAR(100)    NOT NULL,
    description TEXT            NULL,
    created_at  DATETIME        NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (shop_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE CATEGORY (
    category_id     INT             NOT NULL AUTO_INCREMENT,
    parent_id       INT             NULL,
    category_name   VARCHAR(50)     NULL,
    depth           INT             NULL,
    sort_order      INT             NOT NULL DEFAULT 0,
    PRIMARY KEY (category_id),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES CATEGORY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE PRODUCT (
    product_id          INT             NOT NULL AUTO_INCREMENT,
    shop_id             INT             NOT NULL,
    category_id         INT             NOT NULL,
    product_name        VARCHAR(200)    NOT NULL,
    product_description TEXT            NULL,
    product_price       INT             NOT NULL,
    is_visible          BOOLEAN         NULL DEFAULT TRUE,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id),
    CONSTRAINT fk_product_shop     FOREIGN KEY (shop_id)     REFERENCES SHOP (shop_id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES CATEGORY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE PRODUCT_OPTION (
    option_id       INT             NOT NULL AUTO_INCREMENT,
    product_id      INT             NOT NULL,
    color           VARCHAR(50)     NULL,
    size            VARCHAR(20)     NULL,
    stock_qty       INT             NOT NULL DEFAULT 0,
    alert_threshold INT             NULL DEFAULT 10,
    PRIMARY KEY (option_id),
    CONSTRAINT fk_option_product FOREIGN KEY (product_id) REFERENCES PRODUCT (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE PRODUCT_IMAGE (
    image_id    INT             NOT NULL AUTO_INCREMENT,
    product_id  INT             NOT NULL,
    image_url   VARCHAR(500)    NULL,
    is_main     BOOLEAN         NULL DEFAULT TRUE,
    sort_order  INT             NULL DEFAULT 0,
    PRIMARY KEY (image_id),
    CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES PRODUCT (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE STOCK_HISTORY (
    history_id  INT             NOT NULL AUTO_INCREMENT,
    option_id   INT             NOT NULL,
    change_type ENUM('IN', 'OUT', 'ORDER', 'CANCEL') NULL,
    change_qty  INT             NULL,
    before_qty  INT             NOT NULL,
    after_qty   INT             NULL,
    created_at  DATETIME        NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (history_id),
    CONSTRAINT fk_stock_option FOREIGN KEY (option_id) REFERENCES PRODUCT_OPTION (option_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────
-- 3. 부가 도메인 (쿠폰) — ORDERS보다 먼저 생성
-- ───────────────────────────────

CREATE TABLE COUPON (
    coupon_id           INT             NOT NULL AUTO_INCREMENT,
    name                VARCHAR(100)    NULL,
    discount_type       ENUM('RATE', 'FIXED') NULL,
    discount_value      INT             NULL,
    valid_from          DATETIME        NULL,
    valid_until         DATETIME        NULL,
    min_order_amount    INT             NULL DEFAULT 0,
    PRIMARY KEY (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE USER_COUPON (
    user_coupon_id  INT         NOT NULL AUTO_INCREMENT,
    user_id         INT         NOT NULL,
    coupon_id       INT         NOT NULL,
    is_used         BOOLEAN     NULL DEFAULT FALSE,
    used_at         DATETIME    NULL,
    PRIMARY KEY (user_coupon_id),
    CONSTRAINT fk_user_coupon_user   FOREIGN KEY (user_id)   REFERENCES MEMBER_USER (user_id),
    CONSTRAINT fk_user_coupon_coupon FOREIGN KEY (coupon_id) REFERENCES COUPON (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────
-- 4. 주문 도메인
-- ───────────────────────────────

CREATE TABLE CART (
    cart_id     INT         NOT NULL AUTO_INCREMENT,
    user_id     INT         NOT NULL,
    option_id   INT         NOT NULL,
    quantity    INT         NULL DEFAULT 1,
    created_at  DATETIME    NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cart_id),
    CONSTRAINT fk_cart_user   FOREIGN KEY (user_id)   REFERENCES MEMBER_USER (user_id),
    CONSTRAINT fk_cart_option FOREIGN KEY (option_id) REFERENCES PRODUCT_OPTION (option_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ORDERS (
    order_id        INT             NOT NULL AUTO_INCREMENT,
    user_id         INT             NOT NULL,
    address_id      INT             NOT NULL,
    coupon_id       INT             NULL,
    total_price     INT             NULL,
    discount_price  INT             NULL DEFAULT 0,
    final_price     INT             NOT NULL,
    order_state     ENUM('PENDING', 'PAID', 'PREPARING', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL,
    ordered_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id),
    CONSTRAINT fk_orders_user    FOREIGN KEY (user_id)    REFERENCES MEMBER_USER (user_id),
    CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES MEMBER_ADDRESS (address_id),
    CONSTRAINT fk_orders_coupon  FOREIGN KEY (coupon_id)  REFERENCES COUPON (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ORDER_ITEM (
    order_item_id   INT     NOT NULL AUTO_INCREMENT,
    order_id        INT     NOT NULL,
    option_id       INT     NOT NULL,
    quantity        INT     NULL,
    unit_price      INT     NULL,
    PRIMARY KEY (order_item_id),
    CONSTRAINT fk_order_item_order  FOREIGN KEY (order_id)  REFERENCES ORDERS (order_id),
    CONSTRAINT fk_order_item_option FOREIGN KEY (option_id) REFERENCES PRODUCT_OPTION (option_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE RETURN_REQUEST (
    return_id       INT             NOT NULL AUTO_INCREMENT,
    order_item_id   INT             NOT NULL,
    type            ENUM('RETURN', 'EXCHANGE') NOT NULL,
    reason          VARCHAR(500)    NULL,
    status          ENUM('REQUESTED', 'APPROVED', 'REJECTED', 'COMPLETED') NOT NULL,
    requested_at    DATETIME        NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (return_id),
    CONSTRAINT fk_return_order_item FOREIGN KEY (order_item_id) REFERENCES ORDER_ITEM (order_item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────
-- 5. 결제 도메인
-- ───────────────────────────────

CREATE TABLE PAYMENT (
    payment_id          INT             NOT NULL AUTO_INCREMENT,
    order_id            INT             NOT NULL,
    pg_provider         ENUM('TOSSPAYMENTS', 'KGINIIS') NULL,
    pg_transaction_id   VARCHAR(200)    NULL,
    payment_method      ENUM('CARD', 'KAKAO', 'NAVER', 'TRANSFER', 'DEPOSIT') NOT NULL,
    amount              INT             NOT NULL,
    status              ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED') NOT NULL,
    paid_at             DATETIME        NULL,
    PRIMARY KEY (payment_id),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES ORDERS (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE REFUND (
    refund_id       INT             NOT NULL AUTO_INCREMENT,
    payment_id      INT             NOT NULL,
    amount          INT             NULL,
    reason          VARCHAR(500)    NULL,
    status          ENUM('REQUESTED', 'PROCESSING', 'COMPLETED') NULL,
    requested_at    DATETIME        NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at    DATETIME        NULL,
    PRIMARY KEY (refund_id),
    CONSTRAINT fk_refund_payment FOREIGN KEY (payment_id) REFERENCES PAYMENT (payment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────
-- 6. 배송 도메인
-- ───────────────────────────────

CREATE TABLE DELIVERY (
    delivery_id     INT             NOT NULL AUTO_INCREMENT,
    order_id        INT             NOT NULL,
    carrier         VARCHAR(50)     NOT NULL,
    tracking_number VARCHAR(100)    NULL,
    delivery_status ENUM('PREPARING', 'SHIPPED', 'IN_TRANSIT', 'DELIVERED') NULL,
    shipped_at      DATETIME        NULL,
    delivered_at    DATETIME        NULL,
    PRIMARY KEY (delivery_id),
    CONSTRAINT fk_delivery_order FOREIGN KEY (order_id) REFERENCES ORDERS (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ───────────────────────────────
-- 7. 리뷰 도메인
-- ───────────────────────────────

CREATE TABLE REVIEW (
    review_id       INT         NOT NULL AUTO_INCREMENT,
    user_id         INT         NOT NULL,
    product_id      INT         NOT NULL,
    order_item_id   INT         NOT NULL,
    rating          TINYINT     NOT NULL,
    content         TEXT        NULL,
    is_blind        BOOLEAN     NULL DEFAULT FALSE,
    created_at      DATETIME    NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (review_id),
    CONSTRAINT fk_review_user       FOREIGN KEY (user_id)       REFERENCES MEMBER_USER (user_id),
    CONSTRAINT fk_review_product    FOREIGN KEY (product_id)    REFERENCES PRODUCT (product_id),
    CONSTRAINT fk_review_order_item FOREIGN KEY (order_item_id) REFERENCES ORDER_ITEM (order_item_id),
    CONSTRAINT chk_review_rating    CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE REVIEW_IMAGE (
    review_image_id INT             NOT NULL AUTO_INCREMENT,
    review_id       INT             NOT NULL,
    image_url       VARCHAR(500)    NULL,
    sort_order      INT             NOT NULL DEFAULT 0,
    PRIMARY KEY (review_image_id),
    CONSTRAINT fk_review_image_review FOREIGN KEY (review_id) REFERENCES REVIEW (review_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE REVIEW_HELPFUL (
    helpful_id  INT         NOT NULL AUTO_INCREMENT,
    review_id   INT         NOT NULL,
    user_id     INT         NOT NULL,
    created_at  DATETIME    NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (helpful_id),
    CONSTRAINT fk_helpful_review FOREIGN KEY (review_id) REFERENCES REVIEW (review_id),
    CONSTRAINT fk_helpful_user   FOREIGN KEY (user_id)   REFERENCES MEMBER_USER (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE REVIEW_REPLY (
    reply_id        INT     NOT NULL AUTO_INCREMENT,
    review_id       INT     NOT NULL,
    admin_user_id   INT     NULL,
    content         TEXT    NULL,
    created_at      DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (reply_id),
    CONSTRAINT fk_reply_review FOREIGN KEY (review_id) REFERENCES REVIEW (review_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 인덱스 (ERD v1.0 기준 P1/P2/P3)
-- ============================================================

-- P1: 필수 인덱스
CREATE INDEX idx_orders_user_ordered    ON ORDERS       (user_id, ordered_at DESC);
CREATE INDEX idx_payment_order_id       ON PAYMENT      (order_id);
CREATE INDEX idx_refund_payment_id      ON REFUND       (payment_id);
CREATE INDEX idx_stock_option_created   ON STOCK_HISTORY (option_id, created_at DESC);
CREATE INDEX idx_cart_user_id           ON CART         (user_id);

-- P2: 권장 인덱스
CREATE INDEX idx_review_product         ON REVIEW       (product_id, is_blind, created_at DESC);
CREATE INDEX idx_delivery_order_id      ON DELIVERY     (order_id);

-- P3: 선택 인덱스
CREATE INDEX idx_delivery_tracking      ON DELIVERY     (tracking_number);

-- 중복 방지 유니크 인덱스
CREATE UNIQUE INDEX uq_cart_user_option     ON CART           (user_id, option_id);
CREATE UNIQUE INDEX uq_helpful_review_user  ON REVIEW_HELPFUL (review_id, user_id);
CREATE UNIQUE INDEX uq_user_coupon          ON USER_COUPON    (user_id, coupon_id);
