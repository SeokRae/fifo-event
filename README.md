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