import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = "http://localhost:8080/api";
const PERFORMANCE_ID = 1;
const TOTAL_SEATS = 100;

export const options = {
    vus: 200,
    iterations: 200,
    maxDuration: "60s",
};

export default function () {
    const seatsRes = http.get(`${BASE_URL}/performances/${PERFORMANCE_ID}/seats`);
    if (seatsRes.status !== 200) return;

    const seats = seatsRes.json();
    const availableSeats = seats.filter((s) => s.status === "AVAILABLE");
    if (availableSeats.length === 0) return;

    // 2. 좌석 고르기 (Think Time)
    sleep(Math.random() * 0.5);

    // 3. 랜덤 좌석 1개 선택 후 예매
    const seat = availableSeats[Math.floor(Math.random() * availableSeats.length)];

    const payload = JSON.stringify({
        performanceId: PERFORMANCE_ID,
        seatIds: [seat.seatId],
        customerName: `user_${__VU}`,
    });

    http.post(`${BASE_URL}/reservations`, payload, {
        headers: { "Content-Type": "application/json" },
    });
}

export function teardown() {
    sleep(2);

    const res = http.get(`${BASE_URL}/performances/${PERFORMANCE_ID}/stats`);

    check(res, {
        "stats 조회 성공": (r) => r.status === 200,
    });

    if (res.status === 200) {
        const s = res.json();
        console.log(`전체 좌석: ${s.totalSeatCount}`);
        console.log(`SOLD 좌석: ${s.soldSeatCount}`);
        console.log(`예매 건수: ${s.reservationCount}`);
    }
}
