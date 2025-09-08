package ee.aleksale.orderimpl.service;

import ee.aleksale.common.inventory.proto.v1.InventoryOrder;
import ee.aleksale.common.inventory.proto.v1.InventoryType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CurrentOrderService {

    private static final String KEY_DELIMITER = "_";
    private static final String KEY_FORMAT = "%s" + KEY_DELIMITER + "%s";

    private final Map<String, Long> currentOrders = new HashMap<>();

    public void add(InventoryOrder order) {
        currentOrders.put(getKey(order), getTotalQuantity(order));
    }

    public Long getTotalQuantity(InventoryOrder order) {
        var key = getKey(order);
        return currentOrders.getOrDefault(key, 0L) + order.getQuantity();
    }

    public List<InventoryOrder> collectOrderWithTotalQuantity() {
        return currentOrders.entrySet().stream()
                .map(entry -> {
                    var nameTypeSplit = entry.getKey().split(KEY_DELIMITER);
                    return InventoryOrder.newBuilder()
                            .setName(nameTypeSplit[0])
                            .setType(InventoryType.valueOf(nameTypeSplit[1]))
                            .setQuantity(entry.getValue())
                            .build();
                })
                .toList();
    }

    public void clear() {
        currentOrders.clear();
    }

    private String getKey(InventoryOrder order) {
        return String.format(KEY_FORMAT, order.getName(), order.getType());
    }
}
