import http from "k6/http";
import {check} from "k6";

// === 시나리오 2: 다중 좌석 데드락 검증 ===
// VU A: seatIds [1, 2] 예매
// VU B: seatIds [2, 1] 예매
// → FOR UPDATE 순서 차이 → 데드락 발생 가능

const PERFORMANCE_ID = 1;
const BASE_URL = __ENV.BASE_URL || "http://localhost:8080/api";

export const options = {
    vus: 2,
    iterations: 2,
    maxDuration: "30s",
};

export default function () {
    // VU 번호에 따라 좌석 순서를 다르게 지정
    const seatIds = __VU === 1 ? [1, 2] : [2, 1];

    const payload = JSON.stringify({
        performanceId: PERFORMANCE_ID,
        seatIds: seatIds,
        customerName: `user_${__VU}`,
    });

    const res = http.post(`${BASE_URL}/reservations`, payload, {
        headers: {"Content-Type": "application/json"},
    });

    console.log(`[VU ${__VU}] seatIds: ${JSON.stringify(seatIds)} → status: ${res.status}`);

    check(res, {
        "요청이 처리됨 (성공 또는 실패)": (r) =>
            r.status === 201 || r.status === 409 || r.status === 500,
    });
}

export function teardown() {
    const countRes = http.get(
        `${BASE_URL}/reservations/count?performanceId=${PERFORMANCE_ID}`
    ).json();

    console.log("=== 데드락 테스트 결과 ===");
    console.log(`예매 건수:       ${countRes.reservationCount}`);
    console.log(`SOLD 좌석 수:    ${countRes.soldSeatCount}`);
}
