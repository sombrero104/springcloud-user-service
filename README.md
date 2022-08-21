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
<br/><br/><br/><br/>

# 샘플 프로젝트 구성 
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
<img src="./images/response_header_jwt_token.png" width="71%" /><br/>
<br/>

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

#### [bootstrap.yml]
~~~
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: ecommerce
~~~

#### [실행 결과 Bootstrap 로그]
<img src="./images/config_service_bootstrap_log.png" width="60%" /><br/>

#### [실행 결과 Config 정보 확인]
<img src="./images/config_service_test_result.png" width="60%" /><br/>
<br/><br/><br/><br/><br/>

# 마이크로서비스 간 통신 방식 
- Synchronous HTTP communication 
- Asynchronous communication over AMQP
<br/>

## RestTemplate 을 사용하는 방식 
#### [App.java]
~~~
@Bean
public RestTemplate getRestTemplate() {
    return new RestTemplate();
}
~~~
#### [UserServiceImpl.java]
~~~
String orderUrl = String.format(env.getProperty("order_service.url"), userId);
ResponseEntity<List<ResponseOrder>> orderListResponse =
        restTemplate.exchange(orderUrl, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ResponseOrder>>() {
                });
List<ResponseOrder> ordersList = orderListResponse.getBody();
~~~
#### [user-service.yml]
~~~
order_service:
  url: http://127.0.0.1:8000/order-service/%s/orders
       # => order-service의 @GetMapping("/{userId}/orders")로 요청 
~~~
#### [user-service - /users/{userId} 요청 결과] 
<img src="./images/get_users_orders.png" width="60%" /><br/>
<br/>

#### 유레카에 등록된 마이크로서비스 이름으로 변경. 
~~~
http://127.0.0.1:8000/order-service/%s/orders
    => http://order-service/order-service/%s/orders
~~~
~~~
@Bean
@LoadBalanced   // 마이크로서비스 이름으로 호출 가능하도록 @LoadBalanced 추가. 
public RestTemplate getRestTemplate() {
    return new RestTemplate();
}
~~~
<br/>

## FeignClient 를 사용하는 방식 
- REST Call을 추상화 한 Spring Cloud Netflix 라이브러리 
- RestTemplate 보다 직관적이고 간단하게 사용할 수 있음. 
- 호출하려는 HTTP Endpoint 에 대한 Interface 를 생성.
- @FeignClient 선언
- Load balanced 지원 

#### [pom.xml] 
~~~
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
~~~ 
#### [App.java] 
~~~
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients // FeignClient 사용할 수 있도록 추가. 
public class App {
~~~
#### [@FeignClient 인터페이스 생성]
~~~
@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable String userId);

}
~~~
#### [UserServiceImpl.java]
~~~
List<ResponseOrder> ordersList = null;
try {
    ordersList = orderServiceClient.getOrders(userId);
} catch (FeignException ex) {
    log.error(ex.getMessage());
}
~~~
<br/>

### FeignClient 에서 로그 사용
아래와 같이 설정만 해줘도 FeignClient 인터페이스 사용 시 로그가 남는다. <br/>
#### [application.yml]
~~~
logging:
  level:
    me.client: debug
~~~ 
#### [App.java]
~~~
@Bean
public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
}
~~~

#### [요청 결과]

<img src="./images/feignclient_log_01.png" width="59%" /><br/>

<img src="./images/feignclient_log_02.png" width="80%" /><br/>
<br/>

### ErrorDecoder 를 이용한 예외 처리 
FeignClient 사용 시 발생한 예외를 분기별로 처리 가능하게 함. <br/>
ErrorDecoder 를 상속받는 클래스를 만들어서 빈으로 등록하거나 @Component 로 설정한다. <br/>
이렇게 등록하게 되면 FeignClient 인터페이스 호출 시 에러 처리를 해주지 않아도 된다. <br/>

#### [FeignErrorDecoder.java]
~~~
@Component
public class FeignErrorDecoder implements ErrorDecoder {
    /*Environment env;

    @Autowired
    public FeignErrorDecoder(Environment env) {
        this.env = env;
    }*/

    @Override
    public Exception decode(String methodKey, Response response) {
        switch(response.status()) {
            case 400:
                break;
            case 404:
                if (methodKey.contains("getOrders")) {
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                            "User's orders is empty.");
                            // env.getProperty("order_service.exception.orders_is_empty"));
                            
                }
                break;
            default:
                return new Exception(response.reason());
        }

        return null;
    }
}
~~~
#### [UserServiceImpl.java]
~~~
// FeignClient 사용 시 예외처리를 해주지 않아도 됨. 
List<ResponseOrder> ordersList = orderServiceClient.getOrders(userId);
~~~
#### [요청 결과]
<img src="./images/error_decoder_result.png" width="74%" /><br/>
<br/><br/><br/><br/>

# 장애 처리 
## Circuit Breaker
- https://martinfowler.com/bliki/CircuitBreaker.html
- 장애가 발생하는 서비스에 반복적인 호출이 되지 못하게 차단.
- 특정 서비스가 정상적으로 동작하지 않을 경우 다른 기능으로 대체 수행. (장애 회피)
- Circuit Breaker 의 Open/Closed
    - Open(Circuit Breaker 가 열려있는 경우)
        - 특정 수치 만큼 반복적으로 정상적인 서비스가 되지 않을 경우 Circuit Breaker 가 열림. 
    - Closed(Circuit Breaker 가 닫혀있는 경우)
        - 서비스를 정상적으로 이용할 수 있는 경우.  


<br/><br/><br/><br/>
