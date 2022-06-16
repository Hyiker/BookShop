package backend;

public class PricingStrategyFactory {
    private static PricingStrategyFactory instance;


    public static PricingStrategyFactory getInstance() {
        if (instance == null) {
            instance = new PricingStrategyFactory();
        }
        return instance;
    }

    public IPricingStrategy getPricingStrategy(int type) {
        // 对所有的教材类图书实行每本一元的折扣；对连环画类图书提供每本7%的促销折扣；而对非教材类的计算机图书有3%的折扣；对其余书没有折扣。
        switch (type) {
            case ProductSpecification.TEACHING:
                return new FlatRateStrategy(1);
            case ProductSpecification.COMICS:
                return new PercentageStrategy(7);
            case ProductSpecification.COMPUTER:
                return new PercentageStrategy(3);
            case ProductSpecification.OTHER:
                return new NoDiscountStrategy();
            default:
                throw new IllegalArgumentException("Unknown product type: " + type);
        }

    }
}
