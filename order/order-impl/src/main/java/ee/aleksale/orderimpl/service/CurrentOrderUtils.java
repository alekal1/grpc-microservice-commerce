package ee.aleksale.orderimpl.service;

import ee.aleksale.common.inventory.proto.v1.InventoryOrder;
import ee.aleksale.common.inventory.proto.v1.InventoryType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CurrentOrderUtils {

    private final Map<CurrentOrder, CurrentOrderState> currentOrders = new HashMap<>();

    public void add(InventoryOrder order, double price) {
        currentOrders.put(
                new CurrentOrder(order.getName(), order.getType()),
                new CurrentOrderState(getTotalQuantity(order), price)
        );
    }

    public Long getTotalQuantity(InventoryOrder order) {
        var currentOrderState = currentOrders.get(new CurrentOrder(order.getName(), order.getType()));
        long currentQuantity = currentOrderState == null ? 0L : currentOrderState.quantity();
        return currentQuantity + order.getQuantity();
    }

    public List<InventoryOrder> getOrdersWithTotalQuantity() {
        return currentOrders.entrySet().stream()
                .map(entry ->
                        InventoryOrder.newBuilder()
                                .setName(entry.getKey().name())
                                .setType(entry.getKey().type())
                                .setQuantity(entry.getValue().quantity())
                                .build())
                .toList();
    }

    public double getCurrentOrdersTotalSum() {
        return currentOrders.values().stream()
                .mapToDouble(state -> state.quantity() * state.pricePerUnit())
                .sum();
    }

    public void clear() {
        currentOrders.clear();
    }

    private record CurrentOrder(String name, InventoryType type) {
    }

    private record CurrentOrderState(long quantity, double pricePerUnit) {
    }
}
