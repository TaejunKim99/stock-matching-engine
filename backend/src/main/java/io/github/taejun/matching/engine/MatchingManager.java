package io.github.taejun.matching.engine;

import io.github.taejun.matching.domain.order.Order;
import io.github.taejun.matching.domain.order.Trade;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchingManager {
    // 종목별 엔진 관리 (동시성을 위해 ConcurrentHashMap 사용)
    private final Map<String, MatchingEngine> engines = new ConcurrentHashMap<>();

    public MatchingResult process(Order order) {
        // 해당 종목의 엔진이 없으면 새로 생성, 있으면 기존 것 사용
        MatchingEngine engine = engines.computeIfAbsent(order.getSymbol(), s -> new MatchingEngine());

        // 실전에서는 여기서 Redisson 분산 락을 걸어 종목 단위 단일 스레드 처리를 보장함
        // lock.lock(order.getSymbol());
        try {
            return engine.processOrder(order);
        } finally {
            // lock.unlock(order.getSymbol());
        }
    }
}