package model;

public class OrderItem {
	private int orderItemId, orderId, dishId, quantity, itemStatus;
	private Dish dish;
	
	OrderItem(){};
	public int getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(int orderItem) {
		this.orderItemId = orderItem;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getDishId() {
		return dishId;
	}
	public void setDishId(int dishId) {
		this.dishId = dishId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getItemStatus() {
		return itemStatus;
	}
	public void setItemStatus(int itemStatus) {
		this.itemStatus = itemStatus;
	}
	public int getOISubtotal() {
		return quantity*dish.getPrice();
	}
	
}
