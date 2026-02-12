package io.github.taejun.matching.engine;

import io.github.taejun.matching.domain.order.Order;
import io.github.taejun.matching.domain.order.OrderType;
import io.github.taejun.matching.domain.order.Trade;

import java.util.*;

public class MatchingEngine {
    // 특정 종목의 매수/매도 큐만 관리
    private final PriorityQueue<Order> buyQueue = new PriorityQueue<>(
            Comparator.comparingLong(Order::getPrice).reversed().thenComparingLong(Order::getCreatedAt)
    );
    private final PriorityQueue<Order> sellQueue = new PriorityQueue<>(
            Comparator.comparingLong(Order::getPrice).thenComparingLong(Order::getCreatedAt)
    );

    public MatchingResult processOrder(Order newOrder) {
        List<Trade> trades = new ArrayList<>();
        PriorityQueue<Order> oppositeQueue = (newOrder.getType() == OrderType.BUY) ? sellQueue : buyQueue;

        while (!oppositeQueue.isEmpty() && !newOrder.isFullyFilled()) {
            Order bestOpposite = oppositeQueue.peek();

            if (!canMatch(newOrder, bestOpposite)) break;

            int matchQuantity = Math.min(newOrder.getRemainingQuantity(), bestOpposite.getRemainingQuantity());
            long matchPrice = bestOpposite.getPrice();

            newOrder.fill(matchQuantity);
            bestOpposite.fill(matchQuantity);
            trades.add(createTrade(newOrder, bestOpposite, matchPrice, matchQuantity));

            if (bestOpposite.isFullyFilled()) oppositeQueue.poll();
        }

        if (!newOrder.isFullyFilled()) {
            (newOrder.getType() == OrderType.BUY ? buyQueue : sellQueue).offer(newOrder);
        }
        return new MatchingResult(trades, newOrder.getRemainingQuantity(), newOrder.getStatus());
    }

    private boolean canMatch(Order newOrder, Order bestOpposite) {
        return (newOrder.getType() == OrderType.BUY)
                ? newOrder.getPrice() >= bestOpposite.getPrice()
                : newOrder.getPrice() <= bestOpposite.getPrice();
    }

    private Trade createTrade(Order newOrder, Order oppositeOrder, long price, int quantity) {
        boolean isNewOrderBuy = newOrder.getType() == OrderType.BUY;
        return Trade.builder()
                .symbol(newOrder.getSymbol())
                .buyOrderId(isNewOrderBuy ? newOrder.getId() : oppositeOrder.getId())
                .sellOrderId(isNewOrderBuy ? oppositeOrder.getId() : newOrder.getId())
                .price(price)
                .quantity(quantity)
                .build();
    }
}