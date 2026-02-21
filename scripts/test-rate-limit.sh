#!/usr/bin/env bash
# Send 500 requests to test rate limit. Expect: first N get 401, rest get 429 (N = requests-per-minute, default 60).
set -e

BASE_URL="${BASE_URL:-http://localhost:8989}"
TOTAL="${1:-500}"
ENDPOINT="${ENDPOINT:-/auth/login}"

echo "Sending $TOTAL requests to ${BASE_URL}${ENDPOINT}"
echo ""

count_200=0
count_401=0
count_429=0
count_other=0

for i in $(seq 1 "$TOTAL"); do
  code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "${BASE_URL}${ENDPOINT}" \
    -H "Content-Type: application/json" \
    -d '{"username":"u","password":"p"}')
  case "$code" in
    200) ((count_200++)) ;;
    401) ((count_401++)) ;;
    429) ((count_429++)) ;;
    *)   ((count_other++)) ;;
  esac
  # progress every 50
  if [ $((i % 50)) -eq 0 ]; then
    echo "  $i / $TOTAL ..."
  fi
done

echo ""
echo "Summary ($TOTAL requests):"
echo "  200: $count_200"
echo "  401: $count_401"
echo "  429 (rate limited): $count_429"
echo "  other: $count_other"
