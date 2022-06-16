package backend;

public interface IPricingStrategy {
    double getSubtotal(SaleLineItem item);

    String getDescription();
}
