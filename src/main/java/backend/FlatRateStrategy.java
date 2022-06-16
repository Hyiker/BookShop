package backend;

public class FlatRateStrategy implements IPricingStrategy {
    private final double discountPerBook;

    public FlatRateStrategy(double discountPerBook) {
        this.discountPerBook = discountPerBook;
    }

    @Override
    public double getSubtotal(SaleLineItem item) {
        return item.getCopies() * Double.max(0.0, item.getProdSpec().getPrice() - discountPerBook);
    }

    @Override
    public String getDescription() {
        return String.format("-%.3f$/本", discountPerBook);
    }
}
