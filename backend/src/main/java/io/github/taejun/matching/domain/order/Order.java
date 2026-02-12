package io.github.taejun.matching.domain.order;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class Order {
    private final Long id;          // 주문 고유 ID
    private final String userId;    // 사용자 ID
    private final String symbol;    // 종목 코드 (예: SAMSUNG)
    private final OrderType type;   // BUY, SELL
    private final long price;       // 주문 가격
    private final int quantity;     // 주문 총 수량

    private int remainingQuantity;  // 남은 수량
    private OrderStatus status;     // 현재 상태

    // LocalDateTime 대신 정밀한 비교를 위한 long timestamp
    private final long createdAt;   // 우선순위 결정을 위한 시간

    public Order(Long id, String userId, String symbol, OrderType type, long price, int quantity, long timestamp) {
        this.id = id;
        this.userId = userId;
        this.symbol = symbol;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.status = OrderStatus.NEW;
        this.createdAt = timestamp;
    }

    /**
     *  체결 시 수량 차감 및 상태 변경 로직
     */
    public void fill(int fillQuantity) {
        if (fillQuantity <= 0) throw new IllegalArgumentException("체결 수량은 0보다 커야 합니다.");
        if (fillQuantity > this.remainingQuantity) throw new IllegalArgumentException("남은 수량 초과");

        this.remainingQuantity -= fillQuantity;

        // 상태값보다 수량의 변화를 먼저 반영하고 상태를 업데이트 (정합성)
        if (this.remainingQuantity == 0) {
            this.status = OrderStatus.FILLED;
        } else {
            this.status = OrderStatus.PARTIAL_FILLED;
        }
    }

    // 상태값(status)이 아닌 실제 남은 수량을 기준으로 판단 (더 안전함)
    public boolean isFullyFilled() {
        return this.remainingQuantity == 0;
    }
}
