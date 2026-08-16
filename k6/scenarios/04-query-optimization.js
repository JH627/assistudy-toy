/**
 * 시나리오 04: 쿼리 최적화 Before/After 측정
 * 대상: 최적화 후보 Top5 API만 반복 호출
 *   - GET /rooms                       (N+1, 페이지네이션 없음)
 *   - GET /rooms/search?keyword=...    (LIKE '%keyword%' 선행 와일드카드)
 *   - GET /rooms/recommend             (total_time 전체 집계, LIMIT 없음)
 *   - GET /total/ranking               (DATE() 함수로 인덱스 무효화)
 *   - GET /total/grass                 (YEAR() 함수로 인덱스 무효화)
 *   - GET /homeworks/my-participated-rooms (중첩 N+1)
 *
 * 사전 조건:
 *   - scripts/loadtest/seed.sql 로 더미 데이터 시딩 완료
 *   - scripts/loadtest/add_test_user_participation.sql 로 k6 테스트 계정을
 *     loadtest CLASS 방 참가자로 등록 완료 (안 하면 homeworks-participated 결과가 항상 비어서
 *     N+1이 아예 발생하지 않음 - 반드시 먼저 실행할 것)
 *   - TEST_EMAIL, TEST_PASSWORD 환경변수
 *
 * 실행:
 *   k6 run -e BASE_URL=https://api.example.com \
 *          -e TEST_EMAIL=test@test.com -e TEST_PASSWORD=Test@1234 \
 *          -e RUN_LABEL=before \
 *          k6/scenarios/04-query-optimization.js
 *
 * Before/After는 RUN_LABEL만 바꿔서 동일 조건으로 재실행 -> results/ 아래
 * 04-query-optimization-before.json / -after.json 을 비교
 */

import http from 'k6/http';
import { sleep } from 'k6';
import { Trend } from 'k6/metrics';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';
import {
  BASE_URL, authHeaders, checkResponse, login, today,
} from '../utils/helpers.js';

const RUN_LABEL = __ENV.RUN_LABEL || 'run';

export const options = {
  vus: Number(__ENV.VUS) || 15,
  duration: __ENV.DURATION || '60s',
  summaryTrendStats: ['avg', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

// 엔드포인트별 개별 Trend - 요약 JSON에 각각 별도 지표로 나와서 before/after 비교가 쉬움
const trendRoomsList = new Trend('rooms_list_duration');
const trendRoomsSearch = new Trend('rooms_search_duration');
const trendRoomsRecommend = new Trend('rooms_recommend_duration');
const trendTotalRanking = new Trend('total_ranking_duration');
const trendTotalGrass = new Trend('total_grass_duration');
const trendHomeworksParticipated = new Trend('homeworks_participated_duration');

export function setup() {
  const email = __ENV.TEST_EMAIL || 'k6test@test.com';
  const password = __ENV.TEST_PASSWORD || 'Test@1234';

  let accessToken = null;
  for (let retry = 0; retry < 8; retry++) {
    accessToken = login(email, password);
    if (accessToken) break;
    sleep(3); // rate limit 대기 후 재시도
  }
  if (!accessToken) {
    throw new Error('[setup] 로그인 실패 - TEST_EMAIL/TEST_PASSWORD 확인');
  }
  console.log(`[setup] 로그인 성공 - ${email} (RUN_LABEL=${RUN_LABEL})`);
  return { token: accessToken };
}

export default function (data) {
  const headers = authHeaders(data.token);
  const year = new Date().getFullYear();
  let res;

  res = http.get(`${BASE_URL}/rooms`, { headers, tags: { endpoint: 'rooms-list' } });
  checkResponse(res, 'rooms-list');
  trendRoomsList.add(res.timings.duration);
  sleep(0.3);

  res = http.get(`${BASE_URL}/rooms/search?keyword=LT_room`, { headers, tags: { endpoint: 'rooms-search' } });
  checkResponse(res, 'rooms-search');
  trendRoomsSearch.add(res.timings.duration);
  sleep(0.3);

  res = http.get(`${BASE_URL}/rooms/recommend`, { headers, tags: { endpoint: 'rooms-recommend' } });
  checkResponse(res, 'rooms-recommend');
  trendRoomsRecommend.add(res.timings.duration);
  sleep(0.3);

  res = http.get(`${BASE_URL}/time/ranking?date=${today()}`, { headers, tags: { endpoint: 'total-ranking' } });
  checkResponse(res, 'total-ranking');
  trendTotalRanking.add(res.timings.duration);
  sleep(0.3);

  res = http.get(`${BASE_URL}/time/grass?year=${year}`, { headers, tags: { endpoint: 'total-grass' } });
  checkResponse(res, 'total-grass');
  trendTotalGrass.add(res.timings.duration);
  sleep(0.3);

  res = http.get(`${BASE_URL}/homeworks/my-participated-rooms`, { headers, tags: { endpoint: 'homeworks-participated' } });
  checkResponse(res, 'homeworks-participated');
  trendHomeworksParticipated.add(res.timings.duration);
  sleep(0.5);
}

export function handleSummary(data) {
  return {
    [`/results/04-query-optimization-${RUN_LABEL}.json`]: JSON.stringify(data, null, 2),
    [`/results/04-query-optimization-${RUN_LABEL}.html`]: htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  };
}
