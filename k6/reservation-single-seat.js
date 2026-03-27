import http from "k6/http";
import {check} from "k6";

const PERFORMANCE_ID = 1;
const TARGET_SEAT_ID = 1;
const BASE_URL = "http://localhost:8080/api";

export const options = {
    vus: 100,           // 동시 사용자 100명
    iterations: 100,    // 총 100건 (1인 1요청)
    maxDuration: "30s",
};

export default function () {
    const payload = JSON.stringify({
        performanceId: PERFORMANCE_ID,
        seatIds: [TARGET_SEAT_ID],
        customerName: `user_${__VU}`,
    });

    const res = http.post(`${BASE_URL}/reservations`, payload, {
        headers: {"Content-Type": "application/json"},
    });

    check(res, {
        "status is 201 or 409": (r) => r.status === 201 || r.status === 409,
    });
}

export function teardown() {
    const countRes = http.get(
        `${BASE_URL}/reservations/count?performanceId=${PERFORMANCE_ID}`
    ).json();

    console.log("=== 테스트 결과 ===");
    console.log(`예매 건수:       ${countRes.reservationCount}`);
    console.log(`SOLD 좌석 수:    ${countRes.soldSeatCount}`);

    check(countRes, {
        "좌석 1번 예매는 1건만 성공해야 함": (r) => r.reservationCount === 1,
    });
}
