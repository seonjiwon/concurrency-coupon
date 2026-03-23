import http from "k6/http";
import {check} from "k6";

const COUPON_ID = 1;
const BASE_URL = `http://localhost:8090/api/coupons`;

export const options = {
  vus: 100, // 동시 사용자 100명
  iterations: 1000, // 총 요청 1000건 (100명이 나눠서 처리)
  maxDuration: '30s', // 최대 실행 시간
}

export default function () {
  const memberId = __VU * 10 + __ITER + 1;

  const res = http.post(
      `${BASE_URL}/${COUPON_ID}/issue`,
      JSON.stringify(
          {
            memberId: memberId,
          }
      ),
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }
  );

  check(res, {
    'status is 200': (r) => r.status === 200,
  });
};

export function teardown() {
  const coupon = http.get(`${BASE_URL}/${COUPON_ID}`).json();
  const res = http.get(`${BASE_URL}/${COUPON_ID}/issue-count`).json();

  console.log("-- Result --")
  console.log(`총 재고:     ${coupon.totalStock}`);
  console.log(`남은 재고:   ${coupon.availableStock}`);
  console.log(`발급 건수:   ${res.issuedCount}`);

  check(res, {
    "테스트 통과!!!": (r) => r.issuedCount <= coupon.totalStock,
  });
}