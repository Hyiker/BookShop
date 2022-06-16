package backend;

public class PercentageStrategy implements IPricingStrategy {
    private final int discountPercentage;

    public PercentageStrategy(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public double getSubtotal(SaleLineItem item) {
        return item.getCopies() * Double.max(0.0, item.getProdSpec().getPrice() * (100.0 - discountPercentage) / 100.0);
    }

    @Override
    public String getDescription() {
        return String.format("-%d%%/本", discountPercentage);
    }
}
