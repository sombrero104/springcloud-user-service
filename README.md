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
<img src="./images/eureka_server_status_4apps.png" width="95%" /><br/>

<br/><br/><br/><br/>

