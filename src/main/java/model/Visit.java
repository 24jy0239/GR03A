package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Visit {
	int visitId, tableId, totalAmount;
	LocalDateTime visitTime;
	boolean paymentStatus;
	private ArrayList<Order> orders = new ArrayList<>();
	
	Visit(){};
	
	public Visit(int visitId, int tableId, int totalAmount, LocalDateTime visitTime) {
		super();
		this.visitId = visitId;
		this.tableId = tableId;
		this.totalAmount = totalAmount;
		this.visitTime = visitTime;
	}

	public int getVisitId() {
		return visitId;
	}

	public void setVisitId(int visitId) {
		this.visitId = visitId;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount() {
	    this.totalAmount = 0;
	    for (Order order : orders) {
	        this.totalAmount += order.getOSubtotal();
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
