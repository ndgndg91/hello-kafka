### docker compose up
```
local$ sh run.sh
```

### 호스트 파일 수정하여 로컬에서 kafka 접근 가능하게 하기
```
local$ sudo nano /etc/hosts

# 아래 내용 추가
127.0.0.1 kafka-broker1
127.0.0.1 kafka-broker2
127.0.0.1 kafka-broker3
```

### docker compose down
```
local$ sh down.sh
```