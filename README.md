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