package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Visit {
	int visitId, tableNum, totalAmount;
	LocalDateTime visitTime;
	boolean paymentStatus;
	private ArrayList<Order> orders = new ArrayList<>();
	
	Visit(){};
	public int getVisitId() {
		return visitId;
	}

	public void setVisitId(int visitId) {
		this.visitId = visitId;
	}

	public int getTableNum() {
		return tableNum;
	}

	public void setTableNum(int tableNum) {
		this.tableNum = tableNum;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		totalAmount = 0;
		for(Order order : orders) {
			totalAmount+=order.getOSubtotal();
		}
	}

	public LocalDateTime getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(LocalDateTime visitTime) {
		this.visitTime = visitTime;
	}

	public boolean isPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(boolean paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public ArrayList<Order> getOrders(){
		return orders;
	}
	public void setOrders(Order order) {
		orders.add(order);
	}
}
