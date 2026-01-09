package model;

public class Dish {
	private int dishId, price, category;
	private String name, photo;
	private boolean available;
	
	Dish(){};
	Dish(String name, int price, int category, String photo){
		this.name = name;
		this.price = price;
		this.category = category;
		this.photo = photo;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getPrice() {
		return price;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public int getCategory() {
		return category;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getPhoto() {
		return photo;
	}
	public int getDishId() {
		return dishId;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
	public boolean getAvailable() {
		return available;
	}
}
