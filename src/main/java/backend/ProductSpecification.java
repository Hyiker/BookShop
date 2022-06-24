package backend;

public class ProductSpecification {
    // 非教材类的计算机图书、教材类图书、连环画类图书、其他共4种
    public static final int COMPUTER = 0, TEACHING = 1, COMICS = 2, OTHER = 3;

    private String isbn;
    private double price;
    private String title;
    private int type;

    public static String getTypeName(int type) {
        switch (type) {
            case COMPUTER:
                return "计算机";
            case TEACHING:
                return "教材";
            case COMICS:
                return "连环画";
            case OTHER:
                return "其他";
            default:
                throw new IllegalArgumentException("Unknown product type: " + type);
        }
    }

    public ProductSpecification() {
    }

    public ProductSpecification(String isbn, double price, String title, int type) {
        this.isbn = isbn;
        this.price = price;
        this.title = title;
        this.type = type;
    }

    public double getPrice() {
        return price;
    }


    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getType() {
        return type;
    }
}
