package backend;

public class NoDiscountStrategy implements IPricingStrategy {
    @Override
    public double getSubtotal(SaleLineItem item) {
        return item.getCopies() * item.getProdSpec().getPrice();
    }

    @Override
    public String getDescription() {
        return "无优惠";
    }
}
