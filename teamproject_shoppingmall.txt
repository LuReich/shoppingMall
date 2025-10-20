/* ================================================================= */
/* 3조 도소매업 사이트: 최종 데이터베이스 생성 스크립트 (MariaDB)      */
/* (PK를 _uid로, 로그인 ID를 _id로 변경)                             */
/* (오류 수정 및 DROP TABLE IF EXISTS 추가)                        */
/* ================================================================= */

-- 기존 테이블이 존재하면 삭제 (역순으로 삭제하여 FK 제약 조건 문제 방지)
DROP TABLE IF EXISTS `faq`;
DROP TABLE IF EXISTS `seller_inquiry`;
DROP TABLE IF EXISTS `buyer_inquiry`;
DROP TABLE IF EXISTS `ad`;
DROP TABLE IF EXISTS `product_like`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `order_detail`;
DROP TABLE IF EXISTS `order`; -- 'order'는 예약어이므로 백틱으로 감쌈
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `product_image`;
DROP TABLE IF EXISTS `product_detail`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `seller_detail`;
DROP TABLE IF EXISTS `seller`;
DROP TABLE IF EXISTS `buyer_detail`;
DROP TABLE IF EXISTS `buyer`;
DROP TABLE IF EXISTS `admin`;


/* ----------------------------------------------------- */
/* table `admin` (관리자 계정 테이블)                      */
/* ----------------------------------------------------- */
create table admin (
    admin_uid int primary key auto_increment comment '관리자 고유 식별자 (PK)',
    admin_id varchar(50) unique not null comment '관리자 로그인 아이디',
    admin_password varchar(255) not null comment '관리자 비밀번호 (암호화 저장)',
    admin_name varchar(50) not null comment '관리자 이름',
    permission_level tinyint not null default 1 comment '권한 등급 (1: 최고, 9: 최하)',
    create_at datetime default now() comment '계정 생성일',
    update_at datetime default current_timestamp on update current_timestamp comment '계정 수정일'
) comment '관리자 계정 테이블';


/* ----------------------------------------------------- */
/* table `buyer` (구매자 기본 계정 테이블)                 */
/* ----------------------------------------------------- */
create table buyer (
    buyer_uid bigint primary key auto_increment comment '구매자 고유 식별자 (PK)',
    user_id varchar(50) unique not null comment '로그인 아이디',
    password varchar(255) not null comment '비밀번호 (암호화 저장)',
    nickname varchar(50) unique not null comment '닉네임',
    create_at datetime default now() comment '가입일',
    update_at datetime default current_timestamp on update current_timestamp comment '정보 수정일',
    is_active boolean default true comment '계정 활성 여부',
    withdrawal_status enum('VOLUNTARY', 'FORCED_BY_ADMIN') default null comment '탈퇴 상태 (자발적, 강제)',
    withdrawal_reason text default null comment '상세 탈퇴/정지 사유'
) comment '구매자 기본 계정 테이블';


/* ----------------------------------------------------- */
/* table `buyer_detail` (구매자 상세 정보 테이블)           */
/* ----------------------------------------------------- */
create table buyer_detail (
    buyer_uid bigint primary key comment '구매자 고유 식별자 (PK, FK)',
    phone_number varchar(20) unique not null comment '전화번호',
    address varchar(255) not null comment 'API 검색 주소',
    address_detail varchar(255) comment '상세 주소',
    birth date comment '생년월일',
    gender enum('MALE', 'FEMALE') comment '성별',
    foreign key (buyer_uid) references buyer(buyer_uid) on delete cascade
) comment '구매자 상세 정보(개인정보) 테이블';


/* ----------------------------------------------------- */
/* table `seller` (판매자 기본 계정 테이블)                */
/* ----------------------------------------------------- */
create table seller (
    seller_uid bigint primary key auto_increment comment '판매자 고유 식별자 (PK)',
    user_id varchar(50) unique not null comment '로그인 아이디',
    password varchar(255) not null comment '비밀번호 (암호화 저장)',
    company_name varchar(100) not null comment '업체명 (대표 식별자)',
    create_at datetime default now() comment '가입일',
    update_at datetime default current_timestamp on update current_timestamp comment '정보 수정일',
    is_verified boolean default false comment '관리자 인증 여부 (판매 승인)',
    is_active boolean default true comment '계정 활성 여부',
    withdrawal_status enum('VOLUNTARY', 'FORCED_BY_ADMIN') default null comment '탈퇴 상태 (자발적, 강제)',
    withdrawal_reason text default null comment '상세 탈퇴/정지 사유'
) comment '판매자 기본 계정 테이블';


/* ----------------------------------------------------- */
/* table `seller_detail` (판매자 상세 정보 테이블)          */
/* ----------------------------------------------------- */
create table seller_detail (
    seller_uid bigint primary key comment '판매자 고유 식별자 (PK, FK)',
    business_registration_number varchar(20) unique not null comment '사업자 등록 번호',
    company_info text comment '업체 상세 소개',
    phone varchar(20) not null comment '업체 대표 전화번호',
    address varchar(255) not null comment '업체 API 검색 주소',
    address_detail varchar(255) comment '업체 상세 주소',
    foreign key (seller_uid) references seller(seller_uid) on delete cascade
) comment '판매자 상세 정보(사업자정보) 테이블';


/* ----------------------------------------------------- */
/* table `category` (상품 카테고리)                       */
/* ----------------------------------------------------- */
create table category (
    category_id int primary key auto_increment comment '카테고리 고유 번호',
    parent_id int comment '상위 카테고리 ID. 최상위 카테고리는 NULL.',
    category_name varchar(100) not null comment '카테고리명',
    foreign key (parent_id) references category(category_id)
) comment '상품 카테고리 테이블';


/* ----------------------------------------------------- */
/* table `product` (상품 기본 정보 테이블)                */
/* ----------------------------------------------------- */
create table product (
    product_id bigint primary key auto_increment comment '상품 고유 번호',
    seller_uid bigint not null comment '판매자 고유 식별자 (FK)',
    category_id int not null comment '하위 카테고리 ID (FK)',
    product_name varchar(255) not null comment '상품명',
    price int unsigned not null comment '가격',
    stock int unsigned not null comment '재고 수량',
    thumbnail_url varchar(255) comment '대표 이미지 URL (목록 조회 성능용)',
    create_at datetime default now() comment '상품 등록일',
    update_at datetime default current_timestamp on update current_timestamp comment '상품 수정일',
    is_deleted boolean default false comment '상품 삭제 여부 (Soft Delete)',
    foreign key (seller_uid) references seller(seller_uid) on delete cascade,
    foreign key (category_id) references category(category_id)
) comment '상품 기본 정보 테이블 (목록용)';


/* ----------------------------------------------------- */
/* table `product_detail` (상품 상세 정보 테이블)           */
/* ----------------------------------------------------- */
create table product_detail (
    product_id bigint primary key comment '상품 고유 번호 (PK, FK)',
    description text null comment '상품 상세 설명 (HTML 등)',
    shipping_info text comment '배송/반품 정보',
    foreign key (product_id) references product(product_id) on delete cascade
) comment '상품 상세 정보 테이블';


/* ----------------------------------------------------- */
/* table `product_image` (상품 추가 이미지)                 */
/* ----------------------------------------------------- */
create table product_image (
    image_id bigint primary key auto_increment comment '이미지 고유 번호',
    product_id bigint not null comment '상품 ID (FK)',
    image_url varchar(255) not null comment '이미지 파일 경로',
    sort_order tinyint default 0 comment '이미지 표시 순서',
    foreign key (product_id) references product(product_id) on delete cascade
) comment '상품 추가 이미지 테이블';


/* ----------------------------------------------------- */
/* table `cart` (장바구니)                              */
/* ----------------------------------------------------- */
create table cart (
    cart_id bigint primary key auto_increment comment '장바구니 항목 고유 번호',
    buyer_uid bigint not null comment '구매자 고유 식별자 (FK)',
    product_id bigint not null comment '상품 ID (FK)',
    quantity int unsigned not null comment '수량',
    create_at datetime default now() comment '장바구니 추가일',
    update_at datetime default current_timestamp on update current_timestamp comment '수량 변경일',
    unique key (buyer_uid, product_id),
    foreign key (buyer_uid) references buyer(buyer_uid) on delete cascade,
    foreign key (product_id) references product(product_id) on delete cascade
) comment '장바구니 테이블';


/* ----------------------------------------------------- */
/* table `order` (주문 테이블)                             */
/* ----------------------------------------------------- */
-- 'order'는 SQL 예약어이므로 백틱(`)으로 감싸줍니다.
create table `order` (
    order_id bigint primary key auto_increment comment '주문 고유 번호',
    buyer_uid bigint not null comment '구매자 고유 식별자 (FK)',
    total_price int unsigned not null comment '총 결제 금액',
    recipient_name varchar(50) not null comment '수령인 이름',
    recipient_address varchar(255) not null comment '수령인 API 검색 주소',
    recipient_address_detail varchar(255) comment '수령인 상세 주소',
    order_status enum('PENDING', 'PAID', 'SHIPPING', 'DELIVERED', 'CANCELED') not null default 'PENDING' comment '주문 상태',
    create_at datetime default now() comment '주문일',
    update_at datetime default current_timestamp on update current_timestamp comment '주문상태 변경일',
    foreign key (buyer_uid) references buyer(buyer_uid)
) comment '주문 정보 테이블';


/* ----------------------------------------------------- */
/* table `order_detail` (주문 상세)                         */
/* ----------------------------------------------------- */
create table order_detail (
    order_detail_id bigint primary key auto_increment comment '주문 상세 고유 번호',
    order_id bigint not null comment '주문 ID (FK)',
    product_id bigint not null comment '상품 ID (FK)',
    quantity int unsigned not null comment '주문 수량',
    price_per_item int unsigned not null comment '주문 시점의 개당 가격',
    foreign key (order_id) references `order`(order_id) on delete cascade,
    foreign key (product_id) references product(product_id)
) comment '주문 상세 정보 테이블';


/* ----------------------------------------------------- */
/* table `review` (리뷰 테이블)                            */
/* ----------------------------------------------------- */
create table review (
    review_id bigint primary key auto_increment comment '리뷰 고유 번호',
    product_id bigint not null comment '상품 ID (FK)',
    buyer_uid bigint not null comment '작성자 고유 식별자 (FK)',
    order_detail_id bigint unique not null comment '주문 상세 ID (FK, 구매 인증용)',
    rating tinyint unsigned not null comment '별점 (1~5)',
    content text comment '리뷰 내용',
    create_at datetime default now() comment '작성일',
    update_at datetime default current_timestamp on update current_timestamp comment '수정일',
    check (rating >= 1 and rating <= 5),
    foreign key (product_id) references product(product_id),
    foreign key (buyer_uid) references buyer(buyer_uid),
    foreign key (order_detail_id) references order_detail(order_detail_id)
) comment '상품 리뷰 테이블';


/* ----------------------------------------------------- */
/* table `product_like` (상품 좋아요)                       */
/* ----------------------------------------------------- */
create table product_like (
    like_id bigint primary key auto_increment comment '좋아요 고유 번호',
    buyer_uid bigint not null comment '구매자 고유 식별자 (FK)',
    product_id bigint not null comment '상품 ID (FK)',
    create_at datetime default now() comment '좋아요 누른 시간',
    unique key (buyer_uid, product_id),
    foreign key (buyer_uid) references buyer(buyer_uid) on delete cascade,
    foreign key (product_id) references product(product_id) on delete cascade
) comment '상품 좋아요 테이블';


/* ----------------------------------------------------- */
/* table `ad` (광고 테이블)                               */
/* ----------------------------------------------------- */
create table ad (
    ad_id int primary key auto_increment comment '광고 고유 번호',
    seller_uid bigint comment '광고주(판매자) 고유 식별자 (FK, 외부 광고주는 NULL)',
    title varchar(255) not null comment '광고 제목 또는 캠페인명',
    image_url varchar(255) not null comment '광고 이미지 URL',
    link_url varchar(255) not null comment '클릭 시 이동할 URL',
    ad_location enum('MAIN_BANNER', 'CATEGORY_SIDEBAR', 'POPUP') not null comment '광고 노출 위치 (관리/필터링용)',
    start_date date not null comment '광고 시작일',
    end_date date not null comment '광고 종료일',
    click_count int default 0 comment '클릭 수 (성과 측정용)',
    is_active boolean default true comment '관리자에 의한 활성/비활성 여부',
    create_at datetime default now(),
    foreign key (seller_uid) references seller(seller_uid) on delete set null
) comment '광고 정보 테이블';


/* ----------------------------------------------------- */
/* table `buyer_inquiry` (구매자 1:1 문의)                 */
/* ----------------------------------------------------- */
create table buyer_inquiry (
    inquiry_id bigint primary key auto_increment comment '1:1 문의 고유 번호',
    buyer_uid bigint not null comment '문의한 구매자 고유 식별자 (FK)',
    inquiry_type enum('ACCOUNT', 'PAYMENT', 'SHIPPING', 'ETC') not null comment '문의 유형',
    title varchar(255) not null comment '문의 제목',
    question_content text not null comment '문의 내용',
    answer_content text comment '관리자 답변 내용',
    inquiry_status enum('PENDING', 'ANSWERED') not null default 'PENDING' comment '답변 상태',
    create_at datetime default now() comment '문의 등록 시간',
    answer_at datetime comment '답변 등록 시간',
    foreign key (buyer_uid) references buyer(buyer_uid) on delete cascade
) comment '구매자 1:1 문의 테이블';


/* ----------------------------------------------------- */
/* table `seller_inquiry` (판매자 1:1 문의)                */
/* ----------------------------------------------------- */
create table seller_inquiry (
    inquiry_id bigint primary key auto_increment comment '1:1 문의 고유 번호',
    seller_uid bigint not null comment '문의한 판매자 고유 식별자 (FK)',
    inquiry_type enum('ACCOUNT', 'PRODUCT', 'VERIFICATION', 'ETC') not null comment '문의 유형',
    title varchar(255) not null comment '문의 제목',
    question_content text not null comment '문의 내용',
    answer_content text comment '관리자 답변 내용',
    inquiry_status enum('PENDING', 'ANSWERED') not null default 'PENDING' comment '답변 상태',
    create_at datetime default now() comment '문의 등록 시간',
    answer_at datetime comment '답변 등록 시간',
    foreign key (seller_uid) references seller(seller_uid) on delete cascade
) comment '판매자 1:1 문의 테이블';


/* ----------------------------------------------------- */
/* table `faq` (자주 묻는 질문 테이블)                     */
/* ----------------------------------------------------- */
create table faq (
    faq_id int primary key auto_increment comment 'FAQ 고유 번호',
    faq_target enum('BUYER', 'SELLER', 'ALL') not null comment 'FAQ 대상 (구매자, 판매자, 전체)',
    faq_category enum('ACCOUNT', 'PAYMENT', 'SHIPPING', 'PRODUCT', 'ETC') not null comment 'FAQ 카테고리',
    faq_question varchar(255) not null comment '질문',
    faq_answer text not null comment '답변',
    sort_order int default 0 comment '표시 순서', -- 콤마(,) 추가
    create_at datetime default now() comment '등록 시간',
    update_at datetime default current_timestamp on update current_timestamp comment '수정 시간' -- DEFAULT 및 ON UPDATE 추가 (선택 사항)
) comment '자주 묻는 질문(FAQ) 테이블';