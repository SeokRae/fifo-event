## 

- 사전 환경설정

```bash
docker pull mysql
docker run -d -p 3308:3306 -e MYSQL_ROOT_PASSWORD=1234 --name mysql_coupon mysql
docker ps
docker exec -it mysql_coupon bash
```

```bash
mysql -u root -p
create database coupon_example;
use coupon_example;
```

- 프로젝트 세팅

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3308/coupon_example
    username: root
    password: 1234
```

- 요구사항 정의

```text
선착순 100명에게 할인쿠폰을 제공하는 이벤트를 진행하고자 한다.

이 이벤트는 아래와 같은 조건을 만족하여야 한다.
- 선착순 100명에게만 지급되어야한다.
- 101개 이상이 지급되면 안된다.
- 순간적으로 몰리는 트래픽을 버틸 수 있어야합니다.
```

- 동시성 이슈 확인

```text
expected: 100L
 but was: 122L
org.opentest4j.AssertionFailedError: 
expected: 100L
 but was: 122L
```

- 레디스 작업환경 구축

```bash
docker pull redis

docker run --name myredis -d -p 6379:6379 redis
```

- 레디스 접근

```bash
docker exec -it myredis redis-cli 
```

- 레디스 native 명령어

```bash

incr coupon_count

```

- 카프카 작업환경 구축

```yml
version: '2'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
  kafka:
    image: wurstmeister/kafka:2.12-2.5.0
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: 127.0.0.1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```

- 도커 컴포즈 실행 명령어
 
```bash
docker-compose up -d

docker-compose down
```

- 카프카 명령어

```bash
# 토픽 생성
docker exec -it kafka kafka-topics.sh --bootstrap-server localhost:9092 --create --topic testTopic

# 프로듀서 실행
docker exec -it kafka kafka-console-producer.sh --topic testTopic --broker-list 0.0.0.0:9092

# 컨슈머 실행
docker exec -it kafka kafka-console-consumer.sh --topic testTopic --bootstrap-server localhost:9092
```

- topic 생성

```bash
# Topic 생성

docker exec -it kafka kafka-topics.sh --bootstrap-server localhost:9092 --create --topic coupon_create

# Consumer 실행

docker exec -it kafka kafka-console-consumer.sh --topic coupon_create --bootstrap-server localhost:9092 --key-deserializer "org.apache.kafka.common.serialization.StringDeserializer" --value-deserializer "org.apache.kafka.common.serialization.LongDeserializer"
```

- 레디스 SET 명령어

```bash
sadd test 1

1

sadd test 1

0

```