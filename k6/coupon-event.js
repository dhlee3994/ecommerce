import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    scenarios: {
        // 0 ~ 10초
        coupon_event: {
            executor: 'ramping-arrival-rate',
            preAllocatedVUs: 500,
            maxVUS: 10000,
            stages: [
                {duration: '1s', target: 4000},
                {duration: '5s', target: 4000},
                {duration: '4s', target: 2500},
                {duration: '3s', target: 1000},
                {duration: '2s', target: 500},
                {duration: '2s', target: 250},
                {duration: '1s', target: 0},
            ],
            startTime: '0s',
            exec: 'couponEvent',
        },
        // 7초 ~
        product_search: {
            executor: 'ramping-arrival-rate',
            preAllocatedVUs: 500,
            maxVUS: 10000,
            stages: [
                {duration: '3s', target: 500},
                {duration: '10s', target: 2000},
                {duration: '10s', target: 1000},
                {duration: '5s', target: 500},
                {duration: '3s', target: 0},
            ],
            startTime: '7s',
            exec: 'productSearch',
        }
    },
    thresholds: {
        http_req_duration: ['p(95)<50', 'p(99)<100'],
        'http_req_duration{name:couponDetail}': ['p(95)<50', 'p(99)<100'],
        'http_req_duration{name:couponIssue}': ['p(95)<50', 'p(99)<100'],
        'http_req_duration{name:productList}': ['p(95)<50', 'p(99)<100'],
        'http_req_duration{name:productDetail}': ['p(95)<50', 'p(99)<100']
    },
};
const BASE_URL = 'http://localhost:8080/api/v1';

const COUPON_ID = 1;
const USER_IDS = Array.from({length: 10000}, (_, i) => i + 1);

export function couponEvent() {

    // 쿠폰 단건 조회
    const couponResponse = http.get(`${BASE_URL}/coupons/${COUPON_ID}`, {
        tags: {name: 'couponDetail'},
    });
    check(couponResponse, {
        'coupon detail status is 200': (res) => res.status === 200,
    });

    // 쿠폰 발급
    const userId = USER_IDS[Math.floor(Math.random() * USER_IDS.length)];
    const couponIssueResponse = http.post(`${BASE_URL}/coupons/issue`,
        JSON.stringify({userId: userId, couponId: COUPON_ID}), {
            headers: {'Content-Type': 'application/json'},
            tags: {name: 'couponIssue'},
        });
    check(couponIssueResponse, {
        'coupon issue success': (r) => r.status === 200 || r.status === 400,
    });

    sleep((Math.random() * 0.4 + 0.1).toFixed(1));
}

const PRODUCT_NAMES = ['', 'pro', 'duct', 'product', '1', 'not-product'];

export function productSearch() {
    // 상품 목록 조회
    let productListUrl = `${BASE_URL}/products`
    if (Math.random() < 0.4) {
        const searchName = PRODUCT_NAMES[Math.floor(Math.random() * PRODUCT_NAMES.length)];
        productListUrl += `?name=${searchName}`;
    }
    const productListResponse = http.get(productListUrl, {
        tags: {name: 'productList'},
    });
    check(productListResponse, {
        'product list status is 200': (r) => r.status === 200,
    });

    // 상품 단건 조회
    const productIds = JSON.parse(productListResponse.body).data.map(p => p.id);
    if (productIds.length > 0) {
        const productId = productIds[Math.floor(Math.random() * productIds.length)];
        const productDetailResponse = http.get(`${BASE_URL}/products/${productId}`, {
            tags: {name: 'productDetail'},
        });
        check(productDetailResponse, {
            'product detail status is 200': (r) => r.status === 200,
        });
    }

    sleep((Math.random() * 0.4 + 0.1).toFixed(1));
}
