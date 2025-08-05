#!/bin/bash

echo "📦 Elasticsearch에 'search' 인덱스 생성 시도 중..."

response=$(curl -s -o response.json -w "%{http_code}" -X PUT "http://localhost:9200/search" \
  -H "Content-Type: application/json" \
  -d @search-index.json)

if [ "$response" -ge 200 ] && [ "$response" -lt 300 ]; then
  echo "✅ search 인덱스 생성 완료"
else
  echo "❌ 인덱스 생성 실패 (HTTP 상태 코드: $response)"
  echo "🔎 오류 내용:"
  cat response.json
fi
