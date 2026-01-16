package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Order {
	private int orderId, visitId;
	LocalDateTime orderTime;
	private ArrayList<OrderItem> orderItems = new ArrayList<>();

	public Order(){};
	
	public Order(int orderId, int visitId, LocalDateTime orderTime, ArrayList<OrderItem> orderItems) {
		super();
		this.orderId = orderId;
		this.visitId = visitId;
		this.orderTime = orderTime;
		this.orderItems = orderItems;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getVisitId() {
		return visitId;
	}
	public void setVisitId(int visitId) {
		this.visitId = visitId;
	}
	public int getOSubtotal() {
		int Osubtotal = 0;
		for(OrderItem orderItem : orderItems) {
			Osubtotal+=orderItem.getOISubtotal();
		}
		return Osubtotal;
	}
	public ArrayList<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(OrderItem orderItem) {
		orderItems.add(orderItem);
	}
	public LocalDateTime getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(LocalDateTime orderTime) {
		this.orderTime = orderTime;
	}
	
	
}
