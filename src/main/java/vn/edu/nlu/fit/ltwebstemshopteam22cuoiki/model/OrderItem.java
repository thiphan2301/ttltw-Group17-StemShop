package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.model;

public class OrderItem {
    private int productId;
    private String productName;
    private String imageUrl;
    private int quantity;
    private double price;

    // Tạo Constructor không tham số, có tham số và Getter/Setter đầy đủ
    public OrderItem() {}
    public OrderItem(int productId, String productName, String imageUrl, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.price = price;
    }
    
    // Getter/Setter ở đây...
    public String getProductName() { return productName; }
    public String getImageUrl() { return imageUrl; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}