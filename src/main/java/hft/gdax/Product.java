package hft.gdax;

public enum Product {

    BTC_USD("BTC-USD", 100),
    BTC_EUR("BTC-EUR", 100),
    ETH_EUR("ETH-EUR", 100),
    LTC_EUR("LTC-EUR", 100),
    ETH_BTC("ETH-BTC", 100000),
    LTC_BTC("LTC-BTC", 100000);

    private final String id;
    private final long multiplier;

    Product(String id, long multiplier) {
        this.id = id;
        this.multiplier = multiplier;
    }

    public String getId() {
        return id;
    }

    public long getMultiplier() {
        return multiplier;
    }

    public static Product fromProductId(String productId) {
        for (Product product : values()) {
            if (product.id.equalsIgnoreCase(productId)) {
                return product;
            }
        }
        return null;
    }
}