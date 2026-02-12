package io.github.taejun.matching.engine;

import io.github.taejun.matching.domain.order.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchingEngineTest {
    private MatchingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new MatchingEngine();
    }

    @Test
    @DisplayName("1. 전량 체결: 대기 수량과 주문 수량이 일치할 때")
    void fullMatchTest() {
        engine.processOrder(new Order(1L, "user1", "SAMSUNG", OrderType.SELL, 70000, 10, 1000L));

        Order buyOrder = new Order(2L, "user2", "SAMSUNG", OrderType.BUY, 70000, 10, 2000L);
        MatchingResult result = engine.processOrder(buyOrder);

        assertThat(result.getTrades()).hasSize(1);
        assertThat(result.getFinalStatus()).isEqualTo(OrderStatus.FILLED);
        assertThat(result.getRemainingQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("2. 부분 체결: 주문 수량이 대기 수량보다 많을 때")
    void partialMatchTest() {
        engine.processOrder(new Order(1L, "user1", "SAMSUNG", OrderType.SELL, 70000, 10, 1000L));

        Order buyOrder = new Order(2L, "user2", "SAMSUNG", OrderType.BUY, 70000, 15, 2000L);
        MatchingResult result = engine.processOrder(buyOrder);

        assertThat(result.getTrades()).hasSize(1);
        assertThat(result.getRemainingQuantity()).isEqualTo(5);
        assertThat(result.getFinalStatus()).isEqualTo(OrderStatus.PARTIAL_FILLED);
    }

    @Test
    @DisplayName("3. 다중 체결: 동일 가격의 매도 주문들이 순차적으로 체결됨 (버그 수정 완료)")
    void multipleMatchTest() {
        // Given: 70,000원 매도 주문 5주씩 2개 (총 10주 대기)
        engine.processOrder(new Order(1L, "s1", "SAMSUNG", OrderType.SELL, 70000, 5, 1000L));
        engine.processOrder(new Order(2L, "s2", "SAMSUNG", OrderType.SELL, 70000, 5, 1100L));

        // When: 70,000원 매수 10주 주문
        Order buyOrder = new Order(3L, "b1", "SAMSUNG", OrderType.BUY, 70000, 10, 1200L);
        MatchingResult result = engine.processOrder(buyOrder);

        // Then: 2건의 체결이 발생해야 함
        assertThat(result.getTrades()).hasSize(2);
        assertThat(result.getRemainingQuantity()).isEqualTo(0);
        assertThat(result.getFinalStatus()).isEqualTo(OrderStatus.FILLED);
    }

    @Test
    @DisplayName("4. 가격 우선 원칙: 시간은 늦어도 더 저렴한 매도 주문이 먼저 체결됨")
    void pricePriorityTest() {
        // Given: 70,000원(먼저 옴) vs 69,000원(나중에 옴) 매도 주문
        engine.processOrder(new Order(1L, "s1", "SAMSUNG", OrderType.SELL, 70000, 10, 1000L));
        engine.processOrder(new Order(2L, "s2", "SAMSUNG", OrderType.SELL, 69000, 10, 2000L));

        // When: 70,000원 매수 주문
        Order buyOrder = new Order(3L, "b1", "SAMSUNG", OrderType.BUY, 70000, 10, 3000L);
        MatchingResult result = engine.processOrder(buyOrder);

        // Then: 더 싼 69,000원 주문(2번)과 체결되어야 함
        assertThat(result.getTrades().get(0).getSellOrderId()).isEqualTo(2L);
        assertThat(result.getTrades().get(0).getPrice()).isEqualTo(69000);
    }

    @Test
    @DisplayName("5. 시간 우선 원칙: 가격이 같으면 먼저 들어온 주문부터 체결됨")
    void timePriorityTest() {
        // Given: 70,000원 매수 주문 A(1초), B(2초)
        engine.processOrder(new Order(1L, "A", "SAMSUNG", OrderType.BUY, 70000, 10, 1000L));
        engine.processOrder(new Order(2L, "B", "SAMSUNG", OrderType.BUY, 70000, 10, 2000L));

        // When: 70,000원 매도 주문 10주
        Order sellOrder = new Order(3L, "S", "SAMSUNG", OrderType.SELL, 70000, 10, 3000L);
        MatchingResult result = engine.processOrder(sellOrder);

        // Then: 먼저 온 A(1번)와 체결되어야 함
        assertThat(result.getTrades().get(0).getBuyOrderId()).isEqualTo(1L);
    }
}