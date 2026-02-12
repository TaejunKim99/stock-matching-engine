package io.github.taejun.matching.domain.order;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class Trade {
    private final String symbol;
    private final Long buyOrderId;
    private final Long sellOrderId;
    private final long price;
    private final int quantity;
    private final LocalDateTime tradedAt;

    @Builder
    public Trade(String symbol, Long buyOrderId, Long sellOrderId, long price, int quantity) {
        this.symbol = symbol;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
        this.tradedAt = LocalDateTime.now();
    }
}
