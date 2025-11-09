import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Кастомная метрика для отслеживания процента ошибок
const errorRate = new Rate('errors');

// Опции теста
export const options = {
  stages: [
    { duration: '30s', target: 10 },
    { duration: '10s', target: 50 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    // процент ошибок меньше 1%
    errors: ['rate<0.01'],
    // 95% запросов выполняются быстрее 500ms
    http_req_duration: ['p(95)<500'],
    http_req_duration: ['p(99)<1000'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';

export default function () {
  let healthResponse = http.get(`${BASE_URL}/health`);
  let healthCheck = check(healthResponse, {
    'health check status is 200': (r) => r.status === 200,
    'health check response time < 200ms': (r) => r.timings.duration < 200,
  });
  errorRate.add(!healthCheck);
  sleep(0.5);

  let itemsResponse = http.get(`${BASE_URL}/items`);
  let itemsCheck = check(itemsResponse, {
    'get items status is 200': (r) => r.status === 200,
    'get items has data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true && body.data !== undefined;
      } catch (e) {
        return false;
      }
    },
    'get items response time < 500ms': (r) => r.timings.duration < 500,
  });
  errorRate.add(!itemsCheck);
  sleep(0.5);

  const randomId = Math.floor(Math.random() * 10) + 1;
  let itemResponse = http.get(`${BASE_URL}/items/${randomId}`);
  let itemCheck = check(itemResponse, {
    'get item by id status is 200': (r) => r.status === 200,
    'get item by id has data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true && body.data !== undefined;
      } catch (e) {
        return false;
      }
    },
    'get item by id response time < 300ms': (r) => r.timings.duration < 300,
  });
  errorRate.add(!itemCheck);
  sleep(0.5);

  let countResponse = http.get(`${BASE_URL}/items/count`);
  let countCheck = check(countResponse, {
    'get items count status is 200': (r) => r.status === 200,
    'get items count has count': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true && body.data !== undefined && body.data.count !== undefined;
      } catch (e) {
        return false;
      }
    },
    'get items count response time < 200ms': (r) => r.timings.duration < 200,
  });
  errorRate.add(!countCheck);
  sleep(0.5);
}

export function handleSummary(data) {
  const indent = ' ';
  
  let summary = '\n';
  summary += `${indent}=== Тест завершен ===\n\n`;
  
  // Безопасное получение значений с проверкой на существование
  const httpReqs = data.metrics.http_reqs?.values || {};
  const httpDuration = data.metrics.http_req_duration?.values || {};
  const errors = data.metrics.errors?.values || {};
  const httpFailed = data.metrics.http_req_failed?.values || {};
  
  summary += `${indent}Общая статистика:\n`;
  const duration = data.state?.testRunDurationMs ? (data.state.testRunDurationMs / 1000).toFixed(2) : 'N/A';
  summary += `${indent}  Длительность теста: ${duration}s\n`;
  summary += `${indent}  Всего запросов: ${httpReqs.count || 0}\n`;
  summary += `${indent}  Успешных запросов: ${httpDuration.count || 0}\n\n`;
  
  summary += `${indent}Время ответа:\n`;
  summary += `${indent}  Среднее: ${httpDuration.avg ? httpDuration.avg.toFixed(2) : 'N/A'}ms\n`;
  summary += `${indent}  Медиана: ${httpDuration.med ? httpDuration.med.toFixed(2) : 'N/A'}ms\n`;
  summary += `${indent}  P95: ${httpDuration['p(95)'] ? httpDuration['p(95)'].toFixed(2) : 'N/A'}ms\n`;
  summary += `${indent}  P99: ${httpDuration['p(99)'] ? httpDuration['p(99)'].toFixed(2) : 'N/A'}ms\n`;
  summary += `${indent}  Минимум: ${httpDuration.min ? httpDuration.min.toFixed(2) : 'N/A'}ms\n`;
  summary += `${indent}  Максимум: ${httpDuration.max ? httpDuration.max.toFixed(2) : 'N/A'}ms\n\n`;
  
  const errorRateValue = errors.rate || 0;
  const errorCount = errors.count || 0;
  const errorPercent = (errorRateValue * 100).toFixed(2);
  summary += `${indent}Процент ошибок: ${errorPercent}%\n`;
  summary += `${indent}  Всего ошибок: ${errorCount}\n\n`;
  
  if (httpFailed && httpFailed.rate !== undefined) {
    summary += `${indent}HTTP статусы:\n`;
    summary += `${indent}  Успешных: ${((1 - httpFailed.rate) * 100).toFixed(2)}%\n`;
    summary += `${indent}  Ошибок: ${(httpFailed.rate * 100).toFixed(2)}%\n\n`;
  }
  
  const rps = httpReqs.rate || 0;
  summary += `${indent}Пропускная способность: ${rps.toFixed(2)} запросов/сек\n\n`;
  
  console.log(summary);
  
  return {
    'stdout': summary,
    'summary.json': JSON.stringify(data, null, 2),
  };
}

