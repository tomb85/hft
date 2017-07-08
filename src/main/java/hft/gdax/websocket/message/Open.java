package hft.gdax.websocket.message;

public class Open {

    private String type;
    private String time;
    private String product_id;
    private long sequence;
    private String order_id;
    private double price;
    private double remaining_size;
    private String side;
    private double size;

    public String getSymbol() {
        return product_id;
    }

    public long getSequence() {
        return sequence;
    }

    public double getPrice() {
        return price;
    }

    public String getSide() {
        return side;
    }

    public double getSize() {
        return size;
    }
}

/*

{
    "type": "open",
    "time": "2014-11-07T08:19:27.028459Z",
    "product_id": "BTC-USD",
    "sequence": 10,
    "order_id": "d50ec984-77a8-460a-b958-66f114b0de9b",
    "price": "200.2",
    "remaining_size": "1.00",
    "side": "sell"
}

 */