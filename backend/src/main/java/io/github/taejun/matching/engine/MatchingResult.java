package io.github.taejun.matching.engine;

import io.github.taejun.matching.domain.order.OrderStatus;
import io.github.taejun.matching.domain.order.Trade;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class MatchingResult {
    private final List<Trade> trades;        // 발생한 체결 내역들
    private final int remainingQuantity;     // 매칭 후 남은 수량
    private final OrderStatus finalStatus;   // 주문의 최종 상태

    public MatchingResult(List<Trade> trades, int remainingQuantity, OrderStatus finalStatus) {
        this.trades = trades;
        this.remainingQuantity = remainingQuantity;
        this.finalStatus = finalStatus;
    }
}