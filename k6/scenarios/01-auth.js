/**
 * 시나리오 01: 인증 플로우
 * 흐름: 이메일 중복 확인 → 회원가입 → 로그인 → 프로필 조회 → 토큰 갱신 → 로그아웃
 *
 * 실행:
 *   k6 run k6/scenarios/01-auth.js
 *   k6 run -e BASE_URL=http://localhost:8080 k6/scenarios/01-auth.js
 */

import http from 'k6/http';
import { sleep } from 'k6';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import {
  BASE_URL, jsonHeaders, authHeaders,
  checkResponse, uniqueEmail, uniqueNickname,
} from '../utils/helpers.js';

export const options = {
  vus: 10,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<500'],
    // Rate Limit(429) 응답은 정상 동작이므로 임계치에서 제외
    // 실제 서비스 장애 감지용: checks 성공률 95% 이상 기준 사용
    'checks{scenario:default}': ['rate>0.95'],
  },
};

export default function () {
  const email = uniqueEmail();
  const password = 'Test@1234';
  const nickname = uniqueNickname();

  // 1. 이메일 중복 확인
  const checkEmailRes = http.get(
    `${BASE_URL}/users/check-email?email=${email}`,
    { headers: jsonHeaders }
  );
  checkResponse(checkEmailRes, 'check-email');
  sleep(0.5);

  // 2. 닉네임 중복 확인
  const checkNicknameRes = http.get(
    `${BASE_URL}/users/check-nickname?nickname=${nickname}`,
    { headers: jsonHeaders }
  );
  checkResponse(checkNicknameRes, 'check-nickname');
  sleep(0.5);

  // 3. 회원가입
  const signupRes = http.post(
    `${BASE_URL}/users/signup`,
    JSON.stringify({ email, password, nickname }),
    { headers: jsonHeaders }
  );
  checkResponse(signupRes, 'signup', 200);
  sleep(1);

  // 4. 로그인 (429 Rate Limit 허용 - Rate Limiting 동작 확인 시나리오)
  const loginRes = http.post(
    `${BASE_URL}/users/login`,
    JSON.stringify({ email, password }),
    { headers: jsonHeaders }
  );
  if (loginRes.status === 429) {
    // Rate limit 동작 확인 - 재시도 없이 스킵
    sleep(2);
    return;
  }
  checkResponse(loginRes, 'login');
  const authHeader = loginRes.headers['Authorization'];
  const accessToken = authHeader ? authHeader.replace('Bearer ', '') : null;
  if (!accessToken) return;
  sleep(1);

  // 5. 프로필 조회
  const profileRes = http.get(
    `${BASE_URL}/users/profile`,
    { headers: authHeaders(accessToken) }
  );
  checkResponse(profileRes, 'get-profile');
  sleep(1);

  // 6. 토큰 갱신 (refresh_token 쿠키가 Set-Cookie로 내려옴)
  const refreshRes = http.post(
    `${BASE_URL}/users/refresh-token`,
    null,
    {
      headers: { 'Cookie': loginRes.headers['Set-Cookie'] || '' },
    }
  );
  checkResponse(refreshRes, 'refresh-token');
  sleep(1);

  // 7. 로그아웃
  const logoutRes = http.post(
    `${BASE_URL}/users/logout`,
    null,
    { headers: authHeaders(accessToken) }
  );
  checkResponse(logoutRes, 'logout', 204);
  sleep(1);
}

export function handleSummary(data) {
  return {
    '/results/01-auth.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}
