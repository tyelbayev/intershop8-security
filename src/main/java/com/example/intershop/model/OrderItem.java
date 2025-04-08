package com.example.intershop.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_items")
public class OrderItem {

    @Id
    private Long id;

    private Long orderId;
    private Long itemId;

    private int quantity;

    @Transient
    private Item item;

    @Transient
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setItem(Item item) {
        this.item = item;
        this.itemId = item.getId(); // сохраняем ID отдельно
    }

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order.getId(); // аналогично
    }
}
