/**
 * 시나리오 02: 공부방 플로우
 * 흐름: 로그인 → 방 목록 조회 → 방 생성 → 방 참가 → 방 검색 → 방 나가기
 *
 * 사전 조건:
 *   .env 또는 환경변수에 TEST_EMAIL, TEST_PASSWORD 설정
 *   (setup에서 테스트 계정으로 로그인)
 *
 * 실행:
 *   k6 run -e TEST_EMAIL=test@test.com -e TEST_PASSWORD=Test1234! k6/scenarios/02-study-room.js
 */

import http from 'k6/http';
import { sleep } from 'k6';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import {
  BASE_URL, jsonHeaders, authHeaders,
  checkResponse, login, today, randomInt,
} from '../utils/helpers.js';

export const options = {
  vus: 5,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<1500'],
    'checks{scenario:default}': ['rate>0.95'],
  },
};

export function setup() {
  // 테스트 계정 생성 (최초 1회, 이미 있으면 무시)
  const email = __ENV.TEST_EMAIL || 'k6test@test.com';
  const password = __ENV.TEST_PASSWORD || 'Test@1234';

  http.post(
    `${BASE_URL}/users/signup`,
    JSON.stringify({ email, password, nickname: 'k6testuser' }),
    { headers: jsonHeaders }
  );

  // setup()에서 1회 로그인 후 모든 VU가 동일 토큰 공유
  // Rate Limit(1r/s, burst 5) 초기화 대기
  sleep(6);
  let accessToken = null;
  for (let retry = 0; retry < 8; retry++) {
    accessToken = login(email, password);
    if (accessToken) break;
    sleep(3); // Rate Limit 대기 후 재시도
  }

  if (!accessToken) {
    throw new Error('[setup] 로그인 실패 - TEST_EMAIL/TEST_PASSWORD 확인 또는 rate limit 대기 필요');
  }
  console.log(`[setup] 로그인 성공 - ${email}`);
  return { token: accessToken, email, password };
}

export default function (data) {
  const token = data.token;
  if (!token) return;

  const headers = authHeaders(token);

  // 1. 방 목록 조회
  const listRes = http.get(`${BASE_URL}/rooms`, { headers });
  checkResponse(listRes, 'list-rooms');
  sleep(0.5);

  // 2. 내 공부방 목록 조회
  const myRoomsRes = http.get(`${BASE_URL}/rooms/my-study-rooms`, { headers });
  checkResponse(myRoomsRes, 'my-study-rooms');
  sleep(0.5);

  // 3. 공부방 생성 (name 20자 이하, required: type, isPrivate, micActive, maxParticipants)
  const roomName = `k6r${__VU}_${__ITER % 1000}`;
  const createRes = http.post(
    `${BASE_URL}/rooms`,
    JSON.stringify({
      name: roomName,
      description: 'k6 test',
      type: 'STUDY',
      isPrivate: false,
      micActive: false,
      maxParticipants: 10,
    }),
    { headers }
  );
  checkResponse(createRes, 'create-room');
  const roomId = createRes.json('result.id');
  sleep(1);

  // 4. 방 상세 조회
  if (roomId) {
    const detailRes = http.get(`${BASE_URL}/rooms/${roomId}`, { headers });
    checkResponse(detailRes, 'get-room-detail');
    sleep(0.5);

    // 5. 방 검색
    const searchRes = http.get(
      `${BASE_URL}/rooms/search?keyword=k6`,
      { headers }
    );
    checkResponse(searchRes, 'search-rooms');
    sleep(0.5);

    // 6. 방 삭제 (호스트가 직접 삭제)
    const deleteRes = http.del(
      `${BASE_URL}/rooms/${roomId}`,
      null,
      { headers }
    );
    checkResponse(deleteRes, 'delete-room');
    sleep(1);
  }

  // 7. 시간 랭킹 조회
  const rankRes = http.get(
    `${BASE_URL}/time/ranking?date=${today()}`,
    { headers }
  );
  checkResponse(rankRes, 'time-ranking');
  sleep(1);
}

export function handleSummary(data) {
  return {
    '/results/02-study-room.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}
