import http from 'k6/http';
import { check, sleep } from 'k6';

export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

/**
 * Authorization 헤더 포함 기본 헤더 반환
 */
export function authHeaders(token) {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };
}

/**
 * 인증 없는 기본 헤더
 */
export const jsonHeaders = {
  'Content-Type': 'application/json',
};

/**
 * 응답 체크 및 실패 시 로그 출력
 */
export function checkResponse(res, name, expectedStatus = 200) {
  const ok = check(res, {
    [`${name} - status ${expectedStatus}`]: (r) => r.status === expectedStatus,
  });
  if (!ok) {
    console.error(`[FAIL] ${name} | status: ${res.status} | body: ${res.body?.substring(0, 200)}`);
  }
  return ok;
}

/**
 * 로그인 후 accessToken 반환
 * 실패 시 null 반환
 */
export function login(email, password) {
  const res = http.post(
    `${BASE_URL}/users/login`,
    JSON.stringify({ email, password }),
    { headers: jsonHeaders }
  );
  if (res.status !== 200) {
    console.error(`[login] failed - status: ${res.status} - body: ${res.body?.substring(0, 200)}`);
    return null;
  }
  // accessToken은 응답 body가 아닌 Authorization 헤더로 반환됨
  const authHeader = res.headers['Authorization'];
  if (!authHeader) return null;
  return authHeader.replace('Bearer ', '');
}

/**
 * VU별 고유 이메일 생성 (실행마다 고유하도록 타임스탬프 포함)
 */
export function uniqueEmail() {
  const ts = Date.now();
  return `testuser_vu${__VU}_iter${__ITER}_${ts}@test.com`;
}

/**
 * 고유 닉네임 생성 (15자 이하, 실행마다 고유하도록 타임스탬프 포함)
 */
export function uniqueNickname() {
  const ts = Date.now() % 100000;
  return `u${__VU}_${__ITER}_${ts}`.substring(0, 15);
}

/**
 * 1~max 사이 랜덤 정수
 */
export function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

/**
 * 오늘 날짜 (YYYY-MM-DD)
 */
export function today() {
  return new Date().toISOString().split('T')[0];
}
