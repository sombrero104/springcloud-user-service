<br/>

# Service Discovery (Eureka Client) 
<br/>

## Eureka Client 설정 
~~~
server:
  port: 9001

spring:
  application:
    name: user-service

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka
        # Eureka 서버에 서비스를 등록한다. 
~~~

~~~
@SpringBootApplication
@EnableDiscoveryClient
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
~~~
<br/>

## Eureka Client를 다른 실행 방법으로 여러 개 실행해 보기 

### App 1
me.App에서 바로 실행. <br/>

<img src="./images/app1.png" width="70%" /><br/>

### App 2
App 1의 실행 설정을 복사하여 포트만 바꿔서 실행. <br/>

<img src="./images/app2.png" width="70%" /><br/>

### App 3 
인텔리제이에서 아래와 같이 mvn 명령으로 실행. <br/>

<img src="./images/app3.png" width="70%" /><br/>
~~~
mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=9003'
~~~

### App 4
터미널에서 아래와 같이 빌드하여 java 명령으로 직접 실행. <br/>

<img src="./images/app4.png" width="70%" /><br/>
<img src="./images/app4_2.png" width="70%" /><br/>
<img src="./images/app4_3.png" width="70%" /><br/>
~~~
java -jar -Dserver.port=9004 ./target/springcloud-user-service-1.0-SNAPSHOT.jar
~~~ 

### 4개의 App 실행 후 
Eureka Server의 Status에서 위에서 실행한 App들이 등록된 것을 확인할 수 있다. <br/>

<img src="./images/eureka_server_status_4apps.png" width="100%" /><br/>
<br/><br/>

## 랜덤 포트로 App 실행하기
위와 같이 일일이 포트를 설정하여 App을 실행하지 않고 <br/>
App이 실행될 때마다 랜덤 포트를 사용하도록 할 수 있다. <br/>
아래와 같이 application.yml에서 server.port를 0번으로 설정하면 <br/>
실행할 때마다 랜덤 포트로 실행하게 된다. <br/>

#### [application.yml]
~~~
server:
  port: 0 
... 
~~~ 

그런데 App을 여러 개 띄워도 Eureka Server에서 확인해 보면 1개의 서비스만 나오게 된다. <br/>

<img src="./images/random_port_1.png" width="90%" /><br/>

application.yml에서 설정한 포트 번호(0)와 애플리케이션 이름이 같기 때문인데 <br/>
아래와 같이 instance-id를 설정해 주면 해결된다. <br/>
~~~
server:
  port: 0
...
eureka:
  instance:
    instance-id: ${spring.cloud.client.hostname}:${spring.application.instance_id:${random.value}}
... 
~~~
<br/>

<img src="./images/random_port_2.png" width="90%" /><br/>
<br/><br/><br/>

## 샘플 프로젝트 구성 
<img src="./images/ecommerce_application.png" width="60%" /><br/>
<br/>

## user-service 요청 예시 
### 사용자 등록 
- Method: POST
- URL: http://127.0.0.1:8000/user-service/users
- Body: 
~~~
{
    "email": "smith2@email.com",
    "name": "smith2",
    "pwd": "smith111"
}
~~~
- Response:
~~~
{
    "email": "smith2@email.com",
    "name": "smith2",
    "userId": "2964d7a2-d565-405b-88b6-8bea40a2ee82"
}
~~~

### 로그인 
- Method: POST
- URL: http://127.0.0.1:8000/user-service/login
- Body: 
~~~
{
    "email": "smith2@email.com",
    "password": "smith111"
}
~~~
- Response Headers: <br/>
<img src="./images/response_header_jwt_token.png" width="70%" /><br/>
<br/><br/>

## config-service 연동  

#### [pom.xml]
~~~
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
~~~

#### [application.yml]
~~~
spring:
  ...
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: ecommerce
~~~

#### [실행 결과 Bootstrap 로그]
<img src="./images/config_service_bootstrap_log.png" width="60%" /><br/>

#### [실행 결과 Config 정보 확인]
<img src="./images/config_service_test_result.png" width="60%" /><br/>

<br/><br/><br/><br/>
