package model;

/**
 * CartItem（カート内アイテム）
 * セッション内でカート情報を保持
 */
public class CartItem {

	private String dishId; // 料理ID
	private String name; // 料理名
	private int price; // 単価
	private int quantity; // 数量
	private String photo; // 写真ファイル名

	/**
	 * デフォルトコンストラクタ
	 */
	public CartItem() {
	}

	/**
	 * コンストラクタ（Dishから生成）
	 */
	public CartItem(Dish dish, int quantity) {
		this.dishId = dish.getDishId();
		this.name = dish.getName();
		this.price = dish.getPrice();
		this.quantity = quantity;
		this.photo = dish.getPhoto();
	}

	/**
	 * コンストラクタ（全フィールド）
	 */
	public CartItem(String dishId, String name, int price, int quantity, String photo) {
		this.dishId = dishId;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.photo = photo;
	}

	// ==================== Getters and Setters ====================

	public String getDishId() {
		return dishId;
	}

	public void setDishId(String dishId) {
		this.dishId = dishId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	// ==================== ビジネスロジック ====================

	/**
	 * 小計を計算
	 */
	public int getSubtotal() {
		return price * quantity;
	}

	/**
	 * 数量を増やす
	 */
	public void incrementQuantity() {
		this.quantity++;
	}

	/**
	 * 数量を減らす
	 */
	public void decrementQuantity() {
		if (this.quantity > 1) {
			this.quantity--;
		}
	}

	/**
	 * 価格をフォーマット（¥1,000形式）
	 */
	public String getFormattedPrice() {
		return String.format("¥%,d", price);
	}

	/**
	 * 小計をフォーマット（¥1,000形式）
	 */
	public String getFormattedSubtotal() {
		return String.format("¥%,d", getSubtotal());
	}

	/**
	 * 写真のパスを取得
	 */
	public String getPhotoPath() {
		if (photo == null || photo.isEmpty()) {
			return "images/no-image.jpg";
		}
		return "images/" + photo;
	}

	@Override
	public String toString() {
		return "CartItem{" +
				"dishId='" + dishId + '\'' +
				", name='" + name + '\'' +
				", price=" + price +
				", quantity=" + quantity +
				", subtotal=" + getSubtotal() +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CartItem cartItem = (CartItem) o;

		return dishId != null ? dishId.equals(cartItem.dishId) : cartItem.dishId == null;
	}

	@Override
	public int hashCode() {
		return dishId != null ? dishId.hashCode() : 0;
	}
}
