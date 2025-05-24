## event-driven microservices: error handling and communication consistency when using asynchronous communication like Kafka.

success
```
curl -X 'POST' \
  'http://localhost:9191/api/loans/process' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "userId": 101,
  "amount": 100000
}'
```

failure
```
curl -X 'POST' \
  'http://localhost:9191/api/loans/process' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "userId": 202,
  "amount": 100000
}'
```
