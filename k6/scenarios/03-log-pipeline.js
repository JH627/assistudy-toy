/**
 * 시나리오 03: 로그 파이프라인 부하 테스트
 * 흐름: 로그인 → 방 참가 → 로그 전송 (반복) → 분석 결과 조회
 *
 * Kafka 파이프라인:
 *   log-send-service → Kafka → log-process-service → MySQL → common-service 분석 API
 *
 * 실행:
 *   k6 run -e TEST_EMAIL=test@test.com -e TEST_PASSWORD=Test1234! k6/scenarios/03-log-pipeline.js
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
  // 로컬 환경: VU 줄이고 ramp-up으로 Kafka/Docker 부하 분산
  stages: [
    { duration: '15s', target: 3 },  // warm-up
    { duration: '30s', target: 5 },  // 유지
    { duration: '15s', target: 0 },  // cool-down
  ],
  thresholds: {
    // 로그 전송은 Kafka 비동기이므로 빠른 응답 기대
    'http_req_duration{endpoint:send-log}': ['p(95)<300'],
    // 분석 조회는 DB 쿼리 포함으로 여유 있게
    'http_req_duration{endpoint:analysis}': ['p(95)<1000'],
    'checks{scenario:default}': ['rate>0.95'],
  },
};

const LOG_TYPES = ['FOCUS', 'BEHAVIOR', 'GENERAL'];

export function setup() {
  const email = __ENV.TEST_EMAIL || 'k6test@test.com';
  const password = __ENV.TEST_PASSWORD || 'Test@1234';

  // 테스트 계정 생성 (이미 있으면 무시)
  http.post(
    `${BASE_URL}/users/signup`,
    JSON.stringify({ email, password, nickname: 'k6testuser' }),
    { headers: jsonHeaders }
  );

  // Rate Limit(1r/s, burst 5) 초기화 대기
  sleep(6);
  let token = null;
  for (let retry = 0; retry < 8; retry++) {
    token = login(email, password);
    if (token) break;
    sleep(3); // Rate Limit 대기 후 재시도
  }
  if (!token) {
    throw new Error('[setup] 로그인 실패 - TEST_EMAIL/TEST_PASSWORD 확인 또는 rate limit 대기 필요');
  }
  console.log(`[setup] 로그인 성공 - ${email}`);

  // 테스트용 공부방 생성
  const roomRes = http.post(
    `${BASE_URL}/rooms`,
    JSON.stringify({
      name: 'k6-log-test',
      description: 'k6 log pipeline test',
      type: 'STUDY',
      isPrivate: false,
      micActive: false,
      maxParticipants: 50,
    }),
    { headers: authHeaders(token) }
  );

  const roomId = roomRes.json('result.id');
  console.log(`[setup] 테스트 방 생성 완료 - roomId: ${roomId}`);

  return { token, email, password, roomId };
}

export default function (data) {
  if (!data.token || !data.roomId) return;

  let token = data.token;
  const roomId = data.roomId;
  const headers = authHeaders(token);

  // 로그 전송 3회 반복 (로컬 환경 Kafka 부하 완화)
  for (let i = 0; i < 3; i++) {
    const logType = LOG_TYPES[randomInt(0, LOG_TYPES.length - 1)];
    const logRes = http.post(
      `${BASE_URL}/logs/ondevice`,
      JSON.stringify({
        roomId,
        logType,
        focusScore: Math.random() * 100,
        behaviorText: logType === 'BEHAVIOR' ? '집중' : null,
        timestamp: new Date().toISOString().split('.')[0],
        sessionId: `k6-session-vu${__VU}`,
        confidence: Math.random(),
      }),
      {
        headers,
        tags: { endpoint: 'send-log' },
      }
    );
    checkResponse(logRes, `send-log-${logType}`);
    sleep(0.5);
  }

  sleep(2);

  // 분석 결과 조회 (로그 처리 후)
  const focusRes = http.get(
    `${BASE_URL}/analysis/my/focus-scores/date?date=${today()}`,
    {
      headers,
      tags: { endpoint: 'analysis' },
    }
  );
  checkResponse(focusRes, 'analysis-focus-scores');
  sleep(0.5);

  // 공부 시간 요약 조회
  const timeRes = http.get(
    `${BASE_URL}/time/total/summary?roomType=STUDY&date=${today()}`,
    {
      headers,
      tags: { endpoint: 'analysis' },
    }
  );
  checkResponse(timeRes, 'time-summary');
  sleep(1);
}

export function handleSummary(data) {
  return {
    '/results/03-log-pipeline.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}

export function teardown(data) {
  // 테스트 종료 후 방 삭제는 생략
  // (반복 실행 시 방이 누적되므로 필요시 수동 삭제)
  console.log('[teardown] 로그 파이프라인 테스트 완료');
}
