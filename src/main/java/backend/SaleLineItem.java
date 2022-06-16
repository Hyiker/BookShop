package backend;

public class SaleLineItem {
    private int copies;
    private ProductSpecification prodSpec;
    private IPricingStrategy strategy;

    public SaleLineItem(int copies, ProductSpecification prodSpec) {
        this.copies = copies;
        this.prodSpec = prodSpec;
        this.strategy = PricingStrategyFactory.getInstance().getPricingStrategy(prodSpec.getType());
    }

    public ProductSpecification getProdSpec() {
        return prodSpec;
    }

    public int getCopies() {
        return copies;
    }

    public double getSubtotal() {
        return strategy.getSubtotal(this);
    }
}
