package io.github.taejun.matching.engine;

import io.github.taejun.matching.domain.order.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MatchingManagerTest {
    @Test
    @DisplayName("종목 격리 테스트: 삼성과 애플 주문은 가격이 같아도 서로 체결되지 않아야 함")
    void symbolIsolationTest() {
        MatchingManager manager = new MatchingManager();

        // Given: 삼성전자 70,000원 매도 대기
        manager.process(new Order(1L, "u1", "SAMSUNG", OrderType.SELL, 70000, 10, 1000L));

        // When: 애플 70,000원 매수 주문
        MatchingResult result = manager.process(new Order(2L, "u2", "APPLE", OrderType.BUY, 70000, 10, 2000L));

        // Then: 종목이 다르므로 체결 내역이 없어야 함
        assertThat(result.getTrades()).isEmpty();
    }
}