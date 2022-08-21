package me.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4JConfig {
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(4)
                    // Failure Rate Threshold(실패율 임계값): CircuitBreaker 를 언제 열 것인지를 결정하는 수치. (percentage 값)
                    // 디폴트는 50%. (10번 중 5번 실패 시 CircuitBreaker 가 열림.)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                    // CircuitBreaker 를 open 한 상태를 유지하는 지속 시간. (문제가 생긴 서비스를 요청하지 않는 시간.)
                    // 디폴트는 60초. (60초 이후에 다시 문제가 있었던 서비스로 요청을 시도해봄. half-open 상태.)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                    // 정상적으로 서비스를 사용할 수 있을 때 CircuitBreaker 가 close 되고, 지금까지 호출했었던 결과값을 기록하게 되는데
                    // 저장할 때 카운트(횟수) 기반 혹은 시간 기반으로 저장할 것인지를 설정. 디폴트는 카운트 기반.
                .slidingWindowSize(2) // 카운트 기반일 경우에는 횟수, 시간 기반일 경우에는 시간 설정.
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4))
                    // Timeout Duration: 디폴트 1초. (1초 동안 응답이 없을 경우 문제로 간주.)
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build()
        );
    }
}
