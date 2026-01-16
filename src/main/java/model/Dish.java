package model;

public class Dish {
	private int dishId, price;
	private String name, photo, category;
	private boolean available;
	
	public Dish(){};
	public Dish(int dishId, String name, int price, String category, String photo, boolean available){
		this.dishId = dishId;
		this.name = name;
		this.price = price;
		this.category = category;
		this.photo = photo;
		this.available = available;
	}
	public int getDishId() {
		return dishId;
	}
	public void setDishId(int dishId) {
		this.dishId = dishId;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public boolean isAvailable() {
		return available;
	}
	public void setAvailable(boolean available) {
		this.available = available;
	}
}
